package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class MicroServicesConfiguration {

  @JsonProperty
  private String corePlatformUrl;

  public String getCorePlatformUrl() {
    return corePlatformUrl;
  }

}
