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
import com.google.common.annotations.VisibleForTesting;

public class ConnectionStateParams {

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

  public ConnectionStateParams() {}

  @VisibleForTesting
  public ConnectionStateParams(String userId, String targetUserId) {
    this.userId = userId;
    this.targetUserId = targetUserId;
  }


  public String getUserId() {
    return userId;
  }

  public String getTargetUserId() {
    return targetUserId;
  }

}
