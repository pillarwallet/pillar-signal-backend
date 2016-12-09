package org.whispersystems.textsecuregcm.controllers;

public class InvalidEthAddressException extends InvalidAccountPayloadException {
  public InvalidEthAddressException(String expected, String actual){
    super("Invalid Eth Address: Expected " + expected + ", got " + actual);
  }
}
