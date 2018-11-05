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
package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FcmConfiguration {

  @NotNull
  @JsonProperty
  private String projectNumber;

  @NotEmpty
  @JsonProperty
  private String apiKey;

  @NotEmpty
  @JsonProperty
  private String jsonPath;

  public String getProjectNumber() {
    return projectNumber;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getJsonPath() {
    return jsonPath;
  }

}
