package org.ebi.ensembl.repo;

import com.google.protobuf.ProtocolStringList;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.*;
import org.apache.commons.lang3.StringUtils;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.CountResponse;
import org.ebi.ensembl.grpc.common.DBEntry;
import org.ebi.ensembl.grpc.common.Gene;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Objects;

// TODO: Error handling
@ApplicationScoped
public class GeneRepo {
  private final ConnectionHandler connectionHandler;

  public GeneRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Uni<Gene> findByDbId(ConnectionParams params, Integer dbId) {
    return connectionHandler
        .pool(params)
        .preparedQuery(
            """
                SELECT  g.gene_id, g.seq_region_id, g.seq_region_start, g.seq_region_end, g.seq_region_strand, g.analysis_id, 
                  g.biotype, g.display_xref_id, g.description, g.source, g.is_current, g.canonical_transcript_id, g.stable_id, 
                  g.version, g.created_date, g.modified_date, x.display_label, x.dbprimary_acc, x.description, x.version, 
                  exdb.db_name, exdb.status, exdb.db_release, exdb.db_display_name, x.info_type, x.info_text 
                FROM (( (gene g) 
                LEFT JOIN xref x ON x.xref_id = g.display_xref_id ) 
                LEFT JOIN external_db exdb ON exdb.external_db_id = x.external_db_id )  
                WHERE g.gene_id = ? ORDER BY g.gene_id """)
        .execute(Tuple.of(dbId))
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? geneDto(itr.next()) : null);
  }

  public Uni<CountResponse> countAllByBioTypes(
      ConnectionParams params, ProtocolStringList bioTypes) {
    String bioTypesStr = "'" + String.join("','", new ArrayList<>(bioTypes)) + "'";
    return connectionHandler
        .pool(params)
        .query(
            "select count(*) as total from gene where biotype in ("
                + bioTypesStr
                + ") and is_current=1")
        .execute()
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? countDto(itr.next()) : null);
  }

  private CountResponse countDto(Row row) {
    CountResponse.Builder builder = CountResponse.newBuilder();

    if (Objects.isNull(row)) {
      return null;
    }

    if (Objects.nonNull(row.getInteger("total"))) {
      builder.setCount(row.getInteger("total"));
    }

    return builder.build();
  }

  private Gene geneDto(Row r) {
    Gene.Builder geneBuilder = Gene.newBuilder();

    if (Objects.isNull(r)) {
      return null;
    }

    if (Objects.nonNull(r.getInteger("gene_id"))) {
      geneBuilder.setDbId(r.getInteger("gene_id"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_id"))) {
      geneBuilder.setSeqRegionId(r.getInteger("seq_region_id"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_start"))) {
      geneBuilder.setStart(r.getInteger("seq_region_start"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_end"))) {
      geneBuilder.setEnd(r.getInteger("seq_region_end"));
    }

    if (Objects.nonNull(r.getInteger("seq_region_strand"))) {
      geneBuilder.setStrand(r.getInteger("seq_region_strand"));
    }

    if (Objects.nonNull(r.getInteger("analysis_id"))) {
      geneBuilder.setAnalysisId(r.getInteger("analysis_id"));
    }

    if (Objects.nonNull(r.getString("biotype"))) {
      geneBuilder.setStableId(r.getString("biotype"));
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
      geneBuilder.setVersion(r.getInteger("version"));
    }

    if (Objects.nonNull(r.getLocalDateTime("created_date"))) {
      geneBuilder.setCreatedDate(r.getLocalDateTime("created_date").toString());
    }

    if (Objects.nonNull(r.getLocalDateTime("modified_date"))) {
      geneBuilder.setModifiedDate(r.getLocalDateTime("modified_date").toString());
    }

    String xrefDisplayLabel = r.getString("display_label");
    if (StringUtils.isNotEmpty(xrefDisplayLabel)) {
      geneBuilder.setDisplayXref(mapDBEntry(r));
    }

    return geneBuilder.build();
  }

  private DBEntry mapDBEntry(Row r) {
    DBEntry.Builder dbEntryBuilder = DBEntry.newBuilder();

    if (Objects.nonNull(r.getInteger("display_xref_id"))) {
      dbEntryBuilder.setDbId(r.getInteger("display_xref_id"));
    }

    if (Objects.nonNull(r.getString("display_label"))) {
      dbEntryBuilder.setDisplayId(r.getString("display_label"));
    }

    if (Objects.nonNull(r.getString("dbprimary_acc"))) {
      dbEntryBuilder.setPrimaryId(r.getString("dbprimary_acc"));
    }

    if (Objects.nonNull(r.getString("description"))) {
      dbEntryBuilder.setDescription(r.getString("description"));
    }

    if (Objects.nonNull(r.getInteger("version"))) {
      dbEntryBuilder.setVersion(r.getInteger("version"));
    }

    if (Objects.nonNull(r.getString("db_name"))) {
      dbEntryBuilder.setDbname(r.getString("db_name"));
    }

    if (Objects.nonNull(r.getString("db_release"))) {
      dbEntryBuilder.setRelease(r.getString("db_release"));
    }

    if (Objects.nonNull(r.getString("db_display_name"))) {
      dbEntryBuilder.setDbDisplayName(r.getString("db_display_name"));
    }

    if (Objects.nonNull(r.getString("info_type"))) {
      dbEntryBuilder.setInfoType(r.getString("info_type"));
    }

    if (Objects.nonNull(r.getString("info_text"))) {
      dbEntryBuilder.setInfoText(r.getString("info_text"));
    }

    if (Objects.nonNull(r.getString("status"))) {
      dbEntryBuilder.setStatus(r.getString("status"));
    }

    return dbEntryBuilder.build();
  }
}
