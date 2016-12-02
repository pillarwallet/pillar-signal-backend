package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PreKeyStateV2Timestamped {

  @JsonProperty
  @NotEmpty
  private String identityKey;

  @JsonProperty
  @NotNull
  @Valid
  private PreKeyV2 lastResortKey;

  @JsonProperty
  @NotNull
  @Valid
  private List<PreKeyV2> preKeys;

  @JsonProperty
  @NotNull
  @Valid
  private SignedPreKey signedPreKey;

  @JsonProperty
  private long timestamp;



  public PreKeyStateV2Timestamped() {}

  @VisibleForTesting
  public PreKeyStateV2Timestamped(String identityKey, SignedPreKey signedPreKey,
                       List<PreKeyV2> keys, PreKeyV2 lastResortKey, long timestamp)
  {
    this.identityKey   = identityKey;
    this.signedPreKey  = signedPreKey;
    this.preKeys       = keys;
    this.lastResortKey = lastResortKey;
    this.timestamp = timestamp;
  }

  public List<PreKeyV2> getPreKeys() {
    return preKeys;
  }

  public SignedPreKey getSignedPreKey() {
    return signedPreKey;
  }

  public String getIdentityKey() {
    return identityKey;
  }

  public PreKeyV2 getLastResortKey() {
    return lastResortKey;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
