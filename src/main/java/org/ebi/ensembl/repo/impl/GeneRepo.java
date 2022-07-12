package org.ebi.ensembl.repo.impl;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.*;
import org.ebi.ensembl.grpc.Gene;
import org.ebi.ensembl.repo.CoreRepo;
import org.ebi.ensembl.repo.handler.ConnectionHandler;
import org.ebi.ensembl.repo.handler.ConnectionParams;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class GeneRepo implements CoreRepo<Gene> {
  private final ConnectionHandler connectionHandler;

  public GeneRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  @Override
  public Uni<Gene> findByDbId(ConnectionParams params, Integer dbId) {
    return connectionHandler
        .pool(params)
        .preparedQuery("select * from gene where gene_id = ?")
        .execute(Tuple.of(dbId))
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? geneDto(itr.next()) : null);
  }

  private Gene geneDto(Row r) {
    Gene.Builder geneBuilder = Gene.newBuilder();

    if (Objects.isNull(r)) {
      return null;
    }

    if (Objects.nonNull(r.getInteger("gene_id"))) {
      geneBuilder.setDbId(r.getInteger("gene_id"));
    }

    if (Objects.nonNull(r.getString("biotype"))) {
      geneBuilder.setStableId(r.getString("biotype"));
    }

    if (Objects.nonNull(r.getInteger("analysis_id"))) {
      geneBuilder.setAnalysisId(r.getInteger("analysis_id"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_id"))) {
      geneBuilder.setSeqRegionId(r.getInteger("seq_region_id"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_start"))) {
      geneBuilder.setStartPos(r.getInteger("seq_region_start"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_end"))) {
      geneBuilder.setEndPos(r.getInteger("seq_region_end"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_strand"))) {
      geneBuilder.setStrand(r.getInteger("seq_region_strand"));
    }

    if (Objects.nonNull(r.getInteger("display_xref_id"))) {
      geneBuilder.setDisplayXrefId(r.getInteger("display_xref_id"));
    }

    if (Objects.nonNull(r.getString("source"))) {
      geneBuilder.setSource(r.getString("source"));
    }

    if (Objects.nonNull(r.getString("description"))) {
      geneBuilder.setDescription(r.getString("description"));
    }

    if (Objects.nonNull(r.getBoolean("is_current"))) {
      geneBuilder.setIsCurrent(r.getBoolean("is_current"));
    }

    if (Objects.nonNull(r.getInteger("canonical_transcript_id"))) {
      geneBuilder.setCanonicalTranscriptId(r.getInteger("canonical_transcript_id"));
    }

    if (Objects.nonNull(r.getString("stable_id"))) {
      geneBuilder.setStableId(r.getString("stable_id"));
    }

    if (Objects.nonNull(r.getInteger("version"))) {
      geneBuilder.setStableIdVersion(r.getInteger("version"));
    }

    if (Objects.nonNull(r.getLocalDateTime("created_date"))) {
      geneBuilder.setCreatedDate(r.getLocalDateTime("created_date").toString());
    }

    if (Objects.nonNull(r.getLocalDateTime("modified_date"))) {
      geneBuilder.setModifiedDate(r.getLocalDateTime("modified_date").toString());
    }

    return geneBuilder.build();
  }
}
