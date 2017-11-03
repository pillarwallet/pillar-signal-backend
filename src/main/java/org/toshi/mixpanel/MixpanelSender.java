package org.toshi.mixpanel;

import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import com.mixpanel.mixpanelapi.ClientDelivery;

import org.json.JSONObject;
import java.security.MessageDigest;

import io.dropwizard.lifecycle.Managed;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MixpanelSender implements Managed {

  private static class DeliveryThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(DeliveryThread.class);

    private static long MILLIS_TO_WAIT = 10 * 1000;

    public DeliveryThread() {
      mixpanelApi = new MixpanelAPI();
      messageQueue = new ConcurrentLinkedQueue<JSONObject>();
      shutdown = false;
    }

    @Override
    public void run() {
      try {
        while (!this.shutdown) {
          ClientDelivery delivery = new ClientDelivery();
          JSONObject message = null;
          int messageCount = 0;

          do {
            message = messageQueue.poll();
            if (message != null) {
              messageCount += 1;
              delivery.addMessage(message);
            }
          } while (message != null);

          if (messageCount > 0) {
            try {
              mixpanelApi.deliver(delivery);
            } catch (IOException e) {
              logger.error("Failed to deliver mixpanel events", e);
            }
          }

          if (!this.shutdown) Thread.sleep(MILLIS_TO_WAIT);
        }
      } catch (InterruptedException e) {
          logger.info("Mixpanel sender thread inturrupted", e);
      }
    }

    public void sendEvent(JSONObject event) {
      this.messageQueue.add(event);
    }

    public void shutdown() {
      this.shutdown = true;
    }

    private final MixpanelAPI mixpanelApi;
    private final Queue<JSONObject> messageQueue;
    private boolean shutdown;
  }


  private String mixpanelToken;
  private DeliveryThread worker;

  public MixpanelSender(String mixpanelToken) {
    this.mixpanelToken = mixpanelToken;
    this.worker = new DeliveryThread();
  }

  @Override
  public void start() throws Exception {
    this.worker.start();
  }

  @Override
  public void stop() throws Exception {
    this.worker.shutdown();
    this.worker.join();
  }

  public void sendEvent(String distinctId, String eventName, JSONObject properties) {
    MessageBuilder messageBuilder =
      new MessageBuilder(this.mixpanelToken);

    JSONObject event =
      messageBuilder.event(distinctId, eventName, properties);

    this.worker.sendEvent(event);
  }

  public void sendSentMessageEvent(String toshiId) {
    this.sendEvent(MixpanelSender.sha256(toshiId), "Sent Message", null);
  }

  private static String sha256(String base) {
    try{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base.getBytes("UTF-8"));
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    } catch(Exception ex){
       throw new RuntimeException(ex);
    }
}
}
