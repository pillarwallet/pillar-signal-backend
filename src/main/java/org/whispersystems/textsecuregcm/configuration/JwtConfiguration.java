package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class JwtConfiguration {

  @NotEmpty
  @JsonProperty
  private String publicKeyPath;

  public String getPublicKeyPath() {
    return publicKeyPath;
  }

}
