package org.whispersystems.textsecuregcm.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.ECKey.ECDSASignature;
import static org.ethereum.crypto.HashUtil.sha3;
import java.security.SignatureException;
import org.spongycastle.util.encoders.Hex;

import org.whispersystems.textsecuregcm.controllers.InvalidComponentsException;
import org.whispersystems.textsecuregcm.controllers.SignatureLengthException;

public class AccountBootstrap {
  private final Logger logger = LoggerFactory.getLogger(AccountBootstrap.class);

  @JsonProperty
  private AccountBootstrapPayload payload;

  @JsonProperty
  @NotEmpty
  private String signature;

  @JsonProperty
  @NotEmpty
  private String address;

  public AccountBootstrap() {}
  public AccountBootstrap(AccountBootstrapPayload payload, String signature, String address) {
    this.payload = payload;
    this.signature = signature;
    this.address = address;
  }

  public String getSignature() {
    return signature;
  }

  public AccountBootstrapPayload getPayload() {
    return payload;
  }
  public String getAddress() {
    return address;
  }

  public String getRecoveredEthAddress() throws JsonProcessingException,
         SignatureException,
         InvalidComponentsException,
         SignatureLengthException {
    String hexAddress = null;


    if (signature.length() != 132) {
      throw new SignatureLengthException(signature.length());
    }

    ObjectMapper mapper = new ObjectMapper();
    String signablePayload = mapper.writeValueAsString(payload);
    logger.info(signablePayload);

    byte[] hash = sha3(signablePayload.getBytes());
    byte[] sig = Hex.decode(signature.substring(2));

    byte[] r = new byte[32];
    System.arraycopy(sig, 0, r, 0, 32);
    byte[] s = new byte[32];
    System.arraycopy(sig, 32, s, 0, 32);
    byte v = (byte) (sig[64] + 0x1b);

    ECDSASignature signature = ECKey.ECDSASignature.fromComponents(r, s, v);
    if (signature.validateComponents()) {
        byte[] address = ECKey.signatureToAddress(hash, signature);
        hexAddress = "0x"+new String(Hex.encode(address));
    } else {
      throw new InvalidComponentsException();
    }

    return hexAddress;
  }

}
