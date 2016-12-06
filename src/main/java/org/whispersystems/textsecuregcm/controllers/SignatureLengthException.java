package org.whispersystems.textsecuregcm.controllers;

public class SignatureLengthException extends InvalidAccountPayloadException {
  public SignatureLengthException(Integer actualLength){
    super(generateMessage(actualLength));
  }

  private static String generateMessage(Integer actualLength) {
    String message;
    message = "Invalid signature length: "+actualLength+".";
    switch (actualLength) {
      case 130:
        message += " Perhaps you forgot to include the 0x prefix?";
        break;
      default:
        message += " A 65 bit hex-encoded string is expected.";
        break;
    }
    return message;
  }
}
