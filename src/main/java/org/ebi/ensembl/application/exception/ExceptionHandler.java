package org.ebi.ensembl.application.exception;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;

public class ExceptionHandler {
  @ServerExceptionMapper
  public Uni<RestResponse<String>> resourceNotFoundException(ResourceNotFoundException ex) {
    /*
    exception mappers defined in REST endpoint classes will only be called if the exception is thrown in the same class.
    If you want to define global exception mappers, simply define them outside a REST endpoint class
     */
    return Uni.createFrom().item(RestResponse.status(Response.Status.NOT_FOUND, ex.getMessage()));
  }
}
