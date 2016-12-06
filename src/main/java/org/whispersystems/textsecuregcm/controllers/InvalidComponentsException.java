package org.whispersystems.textsecuregcm.controllers;

public class InvalidComponentsException extends InvalidAccountPayloadException {
  public InvalidComponentsException(){
     super("ECDSA Signature components are invalid");
  }
}
