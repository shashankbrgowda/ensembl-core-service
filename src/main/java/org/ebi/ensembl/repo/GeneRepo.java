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

import static org.ebi.ensembl.util.DbUtil.*;

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
    if (Objects.isNull(row)) {
      return null;
    }

    return CountResponse.newBuilder()
            .setCount(protoDefaultValue(row.getInteger("total"), iCls))
            .build();
  }

  private Gene geneDto(Row r) {
    Gene.Builder geneBuilder = Gene.newBuilder();

    if (Objects.isNull(r)) {
      return null;
    }

    String xrefDisplayLabel = r.getString("display_label");
    if (StringUtils.isNotEmpty(xrefDisplayLabel)) {
      geneBuilder.setDisplayXref(mapDBEntry(r));
    }

    return geneBuilder
            .setDbId(protoDefaultValue(r.getInteger("gene_id"), iCls))
            .setSeqRegionId(protoDefaultValue(r.getInteger("seq_region_id"), iCls))
            .setStart(protoDefaultValue(r.getInteger("seq_region_start"), iCls))
            .setEnd(protoDefaultValue(r.getInteger("seq_region_end"), iCls))
            .setStrand(protoDefaultValue(r.getInteger("seq_region_strand"), iCls))
            .setAnalysisId(protoDefaultValue(r.getInteger("analysis_id"), iCls))
            .setStableId(protoDefaultValue(r.getString("biotype"), sCls))
            .setDisplayXrefId(protoDefaultValue(r.getInteger("display_xref_id"), iCls))
            .setSource(protoDefaultValue(r.getString("source"), sCls))
            .setDescription(protoDefaultValue(r.getString("description"), sCls))
            .setIsCurrent(protoDefaultValue(r.getBoolean("is_current"), bCls))
            .setCanonicalTranscriptId(protoDefaultValue(r.getInteger("canonical_transcript_id"), iCls))
            .setStableId(protoDefaultValue(r.getString("stable_id"), sCls))
            .setVersion(protoDefaultValue(r.getInteger("version"), iCls))
            .setCreatedDate(protoDefaultValue(r.getLocalDateTime("created_date"), ldtCls).toString())
            .setModifiedDate(protoDefaultValue(r.getLocalDateTime("modified_date"), ldtCls).toString())
            .build();
  }

  private DBEntry mapDBEntry(Row r) {
    return DBEntry.newBuilder()
            .setDbId(protoDefaultValue(r.getInteger("display_xref_id"), iCls))
            .setDisplayId(protoDefaultValue(r.getString("display_label"), sCls))
            .setPrimaryId(protoDefaultValue(r.getString("dbprimary_acc"), sCls))
            .setDescription(protoDefaultValue(r.getString("description"), sCls))
            .setVersion(protoDefaultValue(r.getInteger("version"), iCls))
            .setDbname(protoDefaultValue(r.getString("db_name"), sCls))
            .setRelease(protoDefaultValue(r.getString("db_release"), sCls))
            .setDbDisplayName(protoDefaultValue(r.getString("db_display_name"), sCls))
            .setInfoType(protoDefaultValue(r.getString("info_type"), sCls))
            .setInfoText(protoDefaultValue(r.getString("info_text"), sCls))
            .setStatus(protoDefaultValue(r.getString("status"), sCls))
            .build();
  }
}
