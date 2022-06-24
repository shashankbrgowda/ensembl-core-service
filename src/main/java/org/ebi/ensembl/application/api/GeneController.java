package org.ebi.ensembl.application.api;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.domain.adaptor.CoreAdaptor;
import org.ebi.ensembl.domain.model.Gene;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("genes")
public class GeneController {
  private final CoreAdaptor<Gene> geneCoreAdaptor;

  public GeneController(CoreAdaptor<Gene> geneCoreAdaptor) {
    this.geneCoreAdaptor = geneCoreAdaptor;
  }

  @GET
  @Path("{dbId}")
  public Uni<Gene> gene(@PathParam("dbId") Integer dbId) {
    return geneCoreAdaptor.findByDbId(dbId);
  }
}
