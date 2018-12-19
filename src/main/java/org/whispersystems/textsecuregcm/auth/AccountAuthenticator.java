/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.auth;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dropwizard.simpleauth.Authenticator;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.Util;

import static com.codahale.metrics.MetricRegistry.name;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

import java.io.FileReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AccountAuthenticator implements Authenticator<BasicCredentials, Account> {

  private final MetricRegistry metricRegistry               = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
  private final Meter          authenticationFailedMeter    = metricRegistry.meter(name(getClass(), "authentication", "failed"   ));
  private final Meter          authenticationSucceededMeter = metricRegistry.meter(name(getClass(), "authentication", "succeeded"));

  private final Logger logger = LoggerFactory.getLogger(AccountAuthenticator.class);

  private final AccountsManager accountsManager;
  private final RSAPublicKey publicKey;

  public AccountAuthenticator(AccountsManager accountsManager) {
    this.accountsManager = accountsManager;
    this.publicKey = null;
  }

  public AccountAuthenticator(AccountsManager accountsManager, String publicKeyPath) throws Exception {
    this.accountsManager = accountsManager;
    this.publicKey = getPublicKey(publicKeyPath);
  }

  @Override
  public Optional<Account> authenticate(BasicCredentials basicCredentials)
      throws AuthenticationException {
    try {
      AuthorizationHeader authorizationHeader = AuthorizationHeader.fromUserAndPassword(basicCredentials.getUsername(), basicCredentials.getPassword());
      Optional<Account>   account             = accountsManager.get(authorizationHeader.getNumber());

      if (!account.isPresent()) {
        return Optional.absent();
      }

      Optional<Device> device = account.get().getDevice(authorizationHeader.getDeviceId());

      if (!device.isPresent()) {
        return Optional.absent();
      }

      if (device.get().getAuthenticationCredentials().verify(basicCredentials.getPassword())) {
        authenticationSucceededMeter.mark();
        account.get().setAuthenticatedDevice(device.get());
        updateLastSeen(account.get(), device.get());
        return account;
      }

      authenticationFailedMeter.mark();
      return Optional.absent();
    } catch (InvalidAuthorizationHeaderException iahe) {
      return Optional.absent();
    }
  }

  public Optional<Account> authenticate(String bearerToken)
          throws AuthenticationException
  {
      try {
        AuthorizationHeader authorizationHeader = AuthorizationHeader.fromBearer(bearerToken, publicKey);
          Optional<Account> account = accountsManager.get(authorizationHeader.getNumber());

          if (!account.isPresent()) {
            return Optional.absent();
          }

          Optional<Device> device = account.get().getDevice(1); // not using multiple device support, 1 is default

          if (!device.isPresent()) {
            return Optional.absent();
          }

          authenticationSucceededMeter.mark();
          account.get().setAuthenticatedDevice(device.get());
          updateLastSeen(account.get(), device.get());
          return account;
      } catch (InvalidAuthorizationHeaderException e) {
          return Optional.absent();
      }

  }

  private void updateLastSeen(Account account, Device device) {
    if (device.getLastSeen() != Util.todayInMillis()) {
      device.setLastSeen(Util.todayInMillis());
      accountsManager.update(account);
    }
  }

  private RSAPublicKey getPublicKey(String filename) throws Exception {
    Scanner in = new Scanner(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    while (in.hasNext()) sb.append(in.next());
    in.close();
    String publicKeyContent = sb.toString()
            .replaceAll("\\n", "")
            .replace(" ", "")
            .replace("-----BEGINPUBLICKEY-----", "")
            .replace("-----ENDPUBLICKEY-----", "");
    KeyFactory kf = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
    RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
    return publicKey;
  }

}
