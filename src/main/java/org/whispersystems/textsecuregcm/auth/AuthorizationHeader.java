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


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.util.Util;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

public class AuthorizationHeader {

  private final String number;
  private final long   accountId;
  private final String password;
  private static final Logger logger = LoggerFactory.getLogger(AuthorizationHeader.class);

  private AuthorizationHeader(String number, long accountId, String password) {
    this.number    = number;
    this.accountId = accountId;
    this.password  = password;
  }

  public static AuthorizationHeader fromUserAndPassword(String user, String password) throws InvalidAuthorizationHeaderException {
    try {
//      String[] numberAndId = user.split("\\.");
//      return new AuthorizationHeader(numberAndId[0],
//                                     numberAndId.length > 1 ? Long.parseLong(numberAndId[1]) : 1,
//                                     password);
//      commenting this out because username with dots gets split and can fail if username is contains another string,
//      using string with dots is valid and safe approach

      return new AuthorizationHeader(user, 1, password);
    } catch (NumberFormatException nfe) {
      throw new InvalidAuthorizationHeaderException(nfe);
    }
  }

  public static AuthorizationHeader fromBearer(String bearerToken, RSAPublicKey publicKey) throws InvalidAuthorizationHeaderException {
    DecodedJWT jwt;
    try {
      Algorithm algorithm = Algorithm.RSA256(publicKey, null);
      JWTVerifier verifier = JWT.require(algorithm).build();
      jwt = verifier.verify(bearerToken);
    } catch (JWTVerificationException e){
      throw new InvalidAuthorizationHeaderException("Failed to verify JWT, " + e.getMessage());
    }

    if (jwt.getClaim("username").isNull())
      throw new InvalidAuthorizationHeaderException("No username in the payload");

    String username = jwt.getClaim("username").asString();

    if (username == null || username.isEmpty())
      throw new InvalidAuthorizationHeaderException("No username in the payload");

    String password = RandomStringUtils.randomAlphanumeric(32);
    // we're not using any password no more and relying on verified JWT
    // random password is generated for account register methods

    return new AuthorizationHeader(username, 1, password);
  }

  public static AuthorizationHeader fromFullHeader(String header) throws InvalidAuthorizationHeaderException {
    return fromFullHeader(header, null);
  }

  public static AuthorizationHeader fromFullHeader(String header, RSAPublicKey publicKey) throws InvalidAuthorizationHeaderException {
    try {
      if (header == null) {
        throw new InvalidAuthorizationHeaderException("Null header");
      }

      String[] headerParts = header.split(" ");

      if (headerParts == null || headerParts.length < 2) {
        throw new InvalidAuthorizationHeaderException("Invalid authorization header: " + header);
      }

      if (!"Basic".equals(headerParts[0])
        && !"Bearer".equals(headerParts[0])) {
        throw new InvalidAuthorizationHeaderException("Unsupported authorization method: " + headerParts[0]);
      }

      if (publicKey != null && "Bearer".equals(headerParts[0])){
        return fromBearer(headerParts[1], publicKey);
      }

      String concatenatedValues = new String(Base64.decode(headerParts[1]));

      if (Util.isEmpty(concatenatedValues)) {
        throw new InvalidAuthorizationHeaderException("Bad decoded value: " + concatenatedValues);
      }

      String[] credentialParts = concatenatedValues.split(":");

      if (credentialParts == null || credentialParts.length < 2) {
        throw new InvalidAuthorizationHeaderException("Badly formated credentials: " + concatenatedValues);
      }

      return fromUserAndPassword(credentialParts[0], credentialParts[1]);
    } catch (IOException ioe) {
      logger.error("InvalidAuthorizationHeaderException: " + ioe.getMessage());
      throw new InvalidAuthorizationHeaderException(ioe);
    }
  }

  public String getNumber() {
    return number;
  }

  public long getDeviceId() {
    return accountId;
  }

  public String getPassword() {
    return password;
  }
}
