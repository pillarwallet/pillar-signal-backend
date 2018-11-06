package org.whispersystems.textsecuregcm.push;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.gcm.server.Message;
import org.whispersystems.gcm.server.Result;
import org.whispersystems.gcm.server.Sender;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.json.JSONException;

import static com.codahale.metrics.MetricRegistry.name;
import io.dropwizard.lifecycle.Managed;

public class GCMSender implements Managed {

  private final Logger logger = LoggerFactory.getLogger(GCMSender.class);

  private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
  private final Meter          success        = metricRegistry.meter(name(getClass(), "sent", "success"));
  private final Meter          failure        = metricRegistry.meter(name(getClass(), "sent", "failure"));
  private final Meter          unregistered   = metricRegistry.meter(name(getClass(), "sent", "unregistered"));
  private final Meter          canonical      = metricRegistry.meter(name(getClass(), "sent", "canonical"));

  private final Map<String, Meter> outboundMeters = new HashMap<String, Meter>() {{
    put("receipt", metricRegistry.meter(name(getClass(), "outbound", "receipt")));
    put("notification", metricRegistry.meter(name(getClass(), "outbound", "notification")));
  }};


  private final AccountsManager   accountsManager;
  private final Sender            signalSender;
  private       ExecutorService   executor;
  private       GoogleCredential  googleCredential;

  public GCMSender(AccountsManager accountsManager, GoogleCredential googleCredential, String projectNumber) {
    this.accountsManager = accountsManager;
    this.googleCredential = googleCredential;
    this.signalSender    = new Sender(50, String.format("https://fcm.googleapis.com/v1/projects/%s/messages:send", projectNumber));
  }

  @VisibleForTesting
  public GCMSender(AccountsManager accountsManager, Sender sender, ExecutorService executor) {
    this.accountsManager  = accountsManager;
    this.signalSender     = sender;
    this.executor         = executor;
    this.googleCredential = new GoogleCredential();
  }

  public void sendMessage(GcmMessage message) {
    sendMessage(message, null);
  }

  public void sendMessage(GcmMessage message, String sender) {
    Message.Builder builder = Message.newBuilder().withDestination(message.getGcmId());

    // token is short lived, flow recommended in Google API documentation,
    // important: it doesn't refresh from remote until it's actually expired
    try {
      googleCredential.refreshToken();
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
      logger.error("Unable to retrieve Google Credentials refresh token: " + e.getMessage());
      return;
    }
    signalSender.setApiKey(googleCredential.getAccessToken());

    String  key     = message.isReceipt() ? "receipt" : "notification";
    String collapseKey = "signal_" + message.getNumber();

    JSONObject msgJSONO = new JSONObject();
    JSONObject apnsJSONO = new JSONObject();
    JSONObject androidJSONO = new JSONObject();

    try {

      msgJSONO.put("type", "signal");
      msgJSONO.put("sender", sender);

      androidJSONO.put("priority","high");
      androidJSONO.put("collapseKey", collapseKey);
      androidJSONO.put("notification", new JSONObject()
              .put("tag", collapseKey));

      apnsJSONO.put("headers", new JSONObject()
              .put("apns-collapse-id", collapseKey));

    } catch (JSONException e) {
      e.printStackTrace();
      logger.error("Unable to generate notification JSON: " + e.getMessage());
      return;
    }

    Message request = builder
            .withAndroid(androidJSONO.toString())
            .withApns(apnsJSONO.toString())
            .withNotificationPart("title", "Pillar Wallet chat")
            .withNotificationPart("body", "New message" + (sender != null && !sender.isEmpty() ? String.format(" from %s", sender) : " received"))
            .withDataPart("msg", msgJSONO.toString())
//            .withDataPart(key, "")
//            commented since not needed and fails on client's FCM foreground push handler
            .build();

    ListenableFuture<Result> future = signalSender.send(request, message);
    markOutboundMeter(key);

    Futures.addCallback(future, new FutureCallback<Result>() {
      @Override
      public void onSuccess(Result result) {
        if (result.isUnregistered() || result.isInvalidRegistrationId()) {
          handleBadRegistration(result);
        } else if (result.hasCanonicalRegistrationId()) {
          handleCanonicalRegistrationId(result);
        } else if (!result.isSuccess()) {
          handleGenericError(result);
        } else {
          success.mark();
        }
      }

      @Override
      public void onFailure(Throwable throwable) {
        logger.warn("GCM Failed: " + throwable);
      }
    }, executor);
  }

  @Override
  public void start() {
    executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void stop() throws IOException {
    this.signalSender.stop();
    this.executor.shutdown();
  }

  private void handleBadRegistration(Result result) {
    GcmMessage message = (GcmMessage)result.getContext();
    logger.warn("Got GCM unregistered notice! " + message.getGcmId());

    Optional<Account> account = getAccountForEvent(message);

    if (account.isPresent()) {
      Device device = account.get().getDevice(message.getDeviceId()).get();
      device.setGcmId(null);
      device.setFetchesMessages(false);

      accountsManager.update(account.get());
    }

    unregistered.mark();
  }

  private void handleCanonicalRegistrationId(Result result) {
    GcmMessage message = (GcmMessage)result.getContext();
    logger.warn(String.format("Actually received 'CanonicalRegistrationId' ::: (canonical=%s), (original=%s)",
                              result.getCanonicalRegistrationId(), message.getGcmId()));

    Optional<Account> account = getAccountForEvent(message);

    if (account.isPresent()) {
      Device device = account.get().getDevice(message.getDeviceId()).get();
      device.setGcmId(result.getCanonicalRegistrationId());

      accountsManager.update(account.get());
    }

    canonical.mark();
  }

  private void handleGenericError(Result result) {
    GcmMessage message = (GcmMessage)result.getContext();
    logger.warn(String.format("Unrecoverable Error ::: (error=%s), (gcm_id=%s), " +
                              "(destination=%s), (device_id=%d)",
                              result.getError(), message.getGcmId(), message.getNumber(),
                              message.getDeviceId()));
    failure.mark();
  }

  private Optional<Account> getAccountForEvent(GcmMessage message) {
    Optional<Account> account = accountsManager.get(message.getNumber());

    if (account.isPresent()) {
      Optional<Device> device = account.get().getDevice(message.getDeviceId());

      if (device.isPresent()) {
        if (message.getGcmId().equals(device.get().getGcmId())) {
          logger.info("GCM Unregister GCM ID matches!");

          if (device.get().getPushTimestamp() == 0 || System.currentTimeMillis() > (device.get().getPushTimestamp() + TimeUnit.SECONDS.toMillis(10)))
          {
            logger.info("GCM Unregister Timestamp matches!");

            return account;
          }
        }
      }
    }

    return Optional.absent();
  }

  private void markOutboundMeter(String key) {
    Meter meter = outboundMeters.get(key);

    if (meter != null) meter.mark();
    else               logger.warn("Unknown outbound key: " + key);
  }
}
