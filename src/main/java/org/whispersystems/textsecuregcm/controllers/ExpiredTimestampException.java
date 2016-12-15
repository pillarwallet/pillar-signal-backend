package org.whispersystems.textsecuregcm.controllers;

public class ExpiredTimestampException extends InvalidAccountPayloadException {
  public ExpiredTimestampException(long desync){
    super("Expired Timestamp: Payload timestamp differed from server time by "+String.valueOf(desync)+" seconds");
  }
}
