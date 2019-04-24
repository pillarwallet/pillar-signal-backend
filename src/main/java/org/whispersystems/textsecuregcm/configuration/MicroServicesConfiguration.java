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

  @JsonProperty
  private String corePlatformUrlInternal;

  @JsonProperty
  private String corePlatformCertPath;

  public String getCorePlatformUrl() {
    return corePlatformUrl;
  }

  public String getCorePlatformUrlInternal() {
    return corePlatformUrlInternal;
  }

  public String getCorePlatformCertPath() {
    return corePlatformCertPath;
  }

}
