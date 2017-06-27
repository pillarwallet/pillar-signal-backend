package org.whispersystems.textsecuregcm.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.annotations.VisibleForTesting;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.ECKey.ECDSASignature;
import static org.ethereum.crypto.HashUtil.sha3;
import java.security.SignatureException;
import org.spongycastle.util.encoders.Hex;

import org.whispersystems.textsecuregcm.controllers.InvalidComponentsException;
import org.whispersystems.textsecuregcm.controllers.SignatureLengthException;

// NOTE: lastResortKey is depreciated, using this so we don't fail if clients include it
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBootstrap {
  private final Logger logger = LoggerFactory.getLogger(AccountBootstrap.class);

  @JsonProperty
  @NotEmpty
  private String identityKey;

  @JsonProperty
  @NotNull
  private String password;

  @JsonProperty
  @NotNull
  @Valid
  private List<PreKey> preKeys;

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

  public AccountBootstrap() {}

  @VisibleForTesting
  public AccountBootstrap(String identityKey,
      SignedPreKey signedPreKey,
      List<PreKey> keys,
      Integer registrationId,
      String signalingKey,
      String password)
  {
    this.identityKey   = identityKey;
    this.signedPreKey  = signedPreKey;
    this.preKeys       = keys;
    this.registrationId = registrationId;
    this.signalingKey = signalingKey;
    this.password = password;
  }

  public List<PreKey> getPreKeys() {
    return preKeys;
  }

  public SignedPreKey getSignedPreKey() {
    return signedPreKey;
  }

  public String getIdentityKey() {
    return identityKey;
  }

  public Integer getRegistrationId() {
    return registrationId;
  }

  public String getSignalingKey() {
    return signalingKey;
  }

  public String getPassword() {
    return password;
  }

}
