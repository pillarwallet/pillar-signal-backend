package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AccountBootstrapPayload {

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
  private Integer registrationId;

  @JsonProperty
  @NotNull
  private String signalingKey;

  @JsonProperty
  @NotNull
  @Valid
  private SignedPreKey signedPreKey;

  @JsonProperty
  private long timestamp;

  public AccountBootstrapPayload() {}

  @VisibleForTesting
  public AccountBootstrapPayload(String identityKey, SignedPreKey signedPreKey,
                       List<PreKeyV2> keys, PreKeyV2 lastResortKey, long timestamp, Integer registrationId, String signalingKey)
  {
    this.identityKey   = identityKey;
    this.signedPreKey  = signedPreKey;
    this.preKeys       = keys;
    this.lastResortKey = lastResortKey;
    this.timestamp = timestamp;
    this.registrationId = registrationId;
    this.signalingKey = signalingKey;
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

  public Integer getRegistrationId() {
    return registrationId;
  }

  public String getSignalingKey() {
    return signalingKey;
  }
}
