package org.toshi.mixpanel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MixpanelConfiguration {
  @JsonProperty
  private String token;

  public String getToken() {
    return token;
  }
}
