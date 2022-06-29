package org.ebi.ensembl.application.api;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.domain.model.Gene;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("genes")
public class GeneResource {
  private final CoreAdaptor<Gene> geneCoreAdaptor;

  public GeneResource(CoreAdaptor<Gene> geneCoreAdaptor) {
    this.geneCoreAdaptor = geneCoreAdaptor;
  }

  @GET
  @Path("{dbId}")
  public Uni<Gene> gene(@PathParam("dbId") Integer dbId) {
    return geneCoreAdaptor.findByDbId(dbId);
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
