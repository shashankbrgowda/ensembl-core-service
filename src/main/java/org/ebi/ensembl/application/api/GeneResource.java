package org.ebi.ensembl.application.api;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("genes")
public class GeneResource {
  private final CoreAdaptor<GeneObj> geneCoreAdaptor;

  public GeneResource(CoreAdaptor<GeneObj> geneCoreAdaptor) {
    this.geneCoreAdaptor = geneCoreAdaptor;
  }

  @POST
  @Path("{dbId}")
  public Uni<GeneObj> gene(@PathParam("dbId") Integer dbId, ConnectionParams connectionParams) {
    return geneCoreAdaptor.fetchByDbId(connectionParams, dbId);
  }

  @GET
  @Path("download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download() throws IOException {
    File file = new File("/Users/shabr/Documents/ENCSR879GEQ.merged.bam.zip");
    return Response.ok(file)
        .header("Content-Disposition", "attachment;filename=" + file.getName())
        .build();
  }
}
