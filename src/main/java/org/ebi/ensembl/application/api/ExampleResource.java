package org.ebi.ensembl.application.api;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.Greeter;
import org.ebi.ensembl.grpc.HelloReply;
import org.ebi.ensembl.grpc.HelloRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello")
public class ExampleResource {
  @GrpcClient private Greeter greeter;

  @GET
  @Path("{name}")
  public Uni<String> hello(@PathParam("name") String name) {
    return greeter
        .sayHello(HelloRequest.newBuilder().setName("Shashank").build())
        .onItem()
        .transform(HelloReply::getMessage);
  }
}
