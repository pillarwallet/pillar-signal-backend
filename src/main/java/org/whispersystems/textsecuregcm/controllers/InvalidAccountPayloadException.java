package org.whispersystems.textsecuregcm.controllers;

public class InvalidAccountPayloadException extends Exception {
  public InvalidAccountPayloadException(String message) {
    super(message);
  }
}
