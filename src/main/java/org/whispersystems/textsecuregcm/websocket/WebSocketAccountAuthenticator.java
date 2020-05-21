package org.whispersystems.textsecuregcm.websocket;

import com.google.common.base.Optional;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.websocket.auth.AuthenticationException;
import org.whispersystems.websocket.auth.WebSocketAuthenticator;

import java.util.List;
import java.util.Map;

import io.dropwizard.auth.basic.BasicCredentials;


public class WebSocketAccountAuthenticator implements WebSocketAuthenticator<Account> {

  private final AccountAuthenticator accountAuthenticator;

  public WebSocketAccountAuthenticator(AccountAuthenticator accountAuthenticator) {
    this.accountAuthenticator = accountAuthenticator;
  }

  @Override
  public Optional<Account> authenticate(UpgradeRequest request) throws AuthenticationException {
    try {
      List<String> subProtocols = request.getSubProtocols();

      if (subProtocols.size() != 0) {
        String bearerToken = subProtocols.get(0);
        if (bearerToken != null && !bearerToken.isEmpty()){
          return accountAuthenticator.authenticate(bearerToken);
        }
      }

      throw new AuthenticationException("Unable to authenticate.");
    } catch (io.dropwizard.auth.AuthenticationException e) {
      throw new AuthenticationException(e);
    }
  }

}
