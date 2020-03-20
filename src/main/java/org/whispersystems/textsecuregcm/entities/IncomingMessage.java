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
package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncomingMessage {

  private final Logger logger = LoggerFactory.getLogger(IncomingMessage.class);

  @JsonProperty
  private int    type;

  @JsonProperty
  private String destination;

  @JsonProperty
  private long   destinationDeviceId = 1;

  @JsonProperty
  private int destinationRegistrationId;

  @JsonProperty
  private String body;

  @JsonProperty
  private String content;

  @JsonProperty
  private String relay;

  @JsonProperty
  private long   timestamp; // deprecated

  @JsonProperty
  private boolean silent = false;

  @JsonProperty
  private String tag;

  @JsonProperty
  private String userConnectionAccessToken;

  @JsonProperty
  private String userId;

  @JsonProperty
  private String targetUserId;

  // left for legacy calls payload validation
  @JsonProperty
  private String sourceIdentityKey;

  // left for legacy calls payload validation
  @JsonProperty
  private String targetIdentityKey;

  public String getDestination() {
    return destination;
  }

  public String getBody() {
    return body;
  }

  public int getType() {
    return type;
  }

  public String getRelay() {
    return relay;
  }

  public long getDestinationDeviceId() {
    return destinationDeviceId;
  }

  public int getDestinationRegistrationId() {
    return destinationRegistrationId;
  }

  public String getContent() {
    return content;
  }

  public boolean isSilent() {
    String tagCheck = getTag();
    logger.info("TAGS CHECK | raw: " + tag + ", getTag()" + tagCheck);
    return silent || (tagCheck != null && tagCheck.equals("tx-note"));
  }

  public String getTag() {
    return tag == null || tag.isEmpty() ? "chat" : tag;
  }

  public String getUserId() {
    return userId;
  }

  public String getTargetUserId() {
    return targetUserId;
  }

}
