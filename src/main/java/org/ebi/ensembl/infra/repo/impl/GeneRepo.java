package org.ebi.ensembl.infra.repo.impl;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.sqlclient.*;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import org.ebi.ensembl.application.model.Gene;
import org.ebi.ensembl.infra.repo.CoreRepo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneRepo implements CoreRepo<Gene> {
  private final Vertx vertx;

  public GeneRepo(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Uni<Gene> findByDbId(Integer dbId) {
    MySQLConnectOptions mySQLConnectOptions =
        new MySQLConnectOptions()
            .setPort(4581)
            .setHost("mysql-ens-havana-prod-1")
            .setDatabase("havana_human_test")
            .setUser("nomerge")
            .setPassword("is_coming");

    // Pool options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    Pool pool = Pool.pool(vertx, mySQLConnectOptions, poolOptions);

    return pool.preparedQuery("select * from gene where gene_id = ?")
            .execute(Tuple.of(dbId))
            .onItem().transform(RowSet::iterator)
            .onItem().transform(itr -> itr.hasNext() ? geneDto(itr.next()) : null);
  }

  private Gene geneDto(Row r) {
    return Gene.builder()
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
            .modifiedDate(r.getLocalDateTime("modified_date")).build();
  }
}
