package org.ebi.ensembl.infra.repo.impl;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.*;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.infra.repo.CoreRepo;
import org.ebi.ensembl.infra.repo.handler.ConnectionHandler;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneRepo implements CoreRepo<GeneObj> {
  private final ConnectionHandler connectionHandler;

  public GeneRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  @Override
  public Uni<GeneObj> findByDbId(ConnectionParams params, Integer dbId) {
    return connectionHandler
        .pool(params)
        .preparedQuery("select * from gene where gene_id = ?")
        .execute(Tuple.of(dbId))
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? geneDto(itr.next()) : null);
  }

  private GeneObj geneDto(Row r) {
    return GeneObj.builder()
        .dbId(r.getInteger("gene_id"))
        .bioType(r.getString("biotype"))
        .analysisId(r.getInteger("analysis_id"))
        .seqRegionId(r.getInteger("seq_region_id"))
        .startPos(r.getInteger("seq_region_start"))
        .endPos(r.getInteger("seq_region_end"))
        .strand(r.getInteger("seq_region_strand"))
        .displayXrefId(r.getInteger("display_xref_id"))
        .source(r.getString("source"))
        .description(r.getString("description"))
        .isCurrent(r.getBoolean("is_current"))
        .canonicalTranscriptId(r.getInteger("canonical_transcript_id"))
        .stableId(r.getString("stable_id"))
        .stableIdVersion(r.getInteger("version"))
        .createdDate(r.getLocalDateTime("created_date"))
        .modifiedDate(r.getLocalDateTime("modified_date"))
        .build();
  }
}
