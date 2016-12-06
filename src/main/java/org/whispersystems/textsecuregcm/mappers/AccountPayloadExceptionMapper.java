package org.whispersystems.textsecuregcm.mappers;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;

import org.whispersystems.textsecuregcm.controllers.InvalidAccountPayloadException;

@Provider
public class AccountPayloadExceptionMapper implements ExceptionMapper<InvalidAccountPayloadException> {
  @Override
  public Response toResponse(InvalidAccountPayloadException ex) {
    return Response.status(400).
      entity(ex.getMessage()).
      type("text/plain").
      build();
  }
}