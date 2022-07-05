package org.ebi.ensembl.application.api;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.domain.service.CoreService;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("genes")
public class GeneResource {
  private final CoreService<GeneObj> geneCoreService;

  public GeneResource(CoreService<GeneObj> geneCoreService) {
    this.geneCoreService = geneCoreService;
  }

  @POST
  @Path("{dbId}")
  public Uni<GeneObj> gene(@PathParam("dbId") Integer dbId, ConnectionParams connectionParams) {
    return geneCoreService.fetchByDbId(connectionParams, dbId);
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
