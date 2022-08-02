package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.apache.commons.lang3.StringUtils;
import org.ebi.ensembl.grpc.common.*;
import org.ebi.ensembl.grpc.transcript.MultiTranscriptResponse;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

import static org.ebi.ensembl.util.DbUtil.*;

@ApplicationScoped
public class TranscriptRepo {
  private final ConnectionHandler connectionHandler;

  public TranscriptRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Multi<Transcript> fetchAllBySlice(ConnectionParams connectionParams, Slice slice) {
    // AND t.seq_region_start <= %d AND t.seq_region_end >= %d
    StringBuilder constraintSb =
            new StringBuilder(String.format(" t.is_current = 1 AND t.seq_region_id = %d ",
                    slice.getSeqRegionId(), slice.getStart(), slice.getEnd()));
    return connectionHandler
            .pool(connectionParams)
            .query(
                    """
                        SELECT  t.transcript_id, t.seq_region_id, t.seq_region_start, t.seq_region_end, t.seq_region_strand, 
                        t.analysis_id, t.gene_id, t.is_current, t.stable_id, t.version, created_date, modified_date, 
                        t.description, t.biotype, exdb.db_name, exdb.status, exdb.db_display_name, x.xref_id, x.display_label, 
                        x.dbprimary_acc, x.version, x.description, x.info_type, x.info_text, exdb.db_release, t.source
                        FROM (( (transcript t)
                          LEFT JOIN xref x ON x.xref_id = t.display_xref_id )
                          LEFT JOIN external_db exdb ON exdb.external_db_id = x.external_db_id )
                         WHERE """ + constraintSb)
            .execute()
            .onItem()
            .transformToMulti(rows -> Multi.createFrom().iterable(rows))
            .onItem()
            .transform(this::transcriptDto);
  }

  public Uni<Transcript> findByDbId(ConnectionParams connectionParams, Integer dbId) {
    String sql = String.format("""
                        SELECT  t.transcript_id, t.seq_region_id, t.seq_region_start, t.seq_region_end, t.seq_region_strand, 
                         t.analysis_id, t.gene_id, t.is_current, t.stable_id, t.version, created_date, modified_date, t.description, 
                         t.biotype, exdb.db_name, exdb.status, exdb.db_display_name, x.xref_id, x.display_label, x.dbprimary_acc, 
                         x.version, x.description, x.info_type, x.info_text, exdb.db_release, t.source
                        FROM (( (transcript t)
                          LEFT JOIN xref x ON x.xref_id = t.display_xref_id )
                          LEFT JOIN external_db exdb ON exdb.external_db_id = x.external_db_id )
                         WHERE t.transcript_id = %d AND t.is_current = 1
                         ORDER BY t.transcript_id """, dbId);
    return connectionHandler
            .pool(connectionParams)
            .query(sql)
            .execute()
            .onItem()
            .transform(RowSet::iterator)
            .onItem()
            .transform(itr -> itr.hasNext() ? transcriptDto(itr.next()) : Transcript.newBuilder().build());
  }

  public Uni<Transcript> findByStableId(ConnectionParams connectionParams, String stableId) {
    String sql = String.format("""
                        SELECT  t.transcript_id, t.seq_region_id, t.seq_region_start, t.seq_region_end, t.seq_region_strand, 
                         t.analysis_id, t.gene_id, t.is_current, t.stable_id, t.version, created_date, modified_date, t.description, 
                         t.biotype, exdb.db_name, exdb.status, exdb.db_display_name, x.xref_id, x.display_label, x.dbprimary_acc, 
                         x.version, x.description, x.info_type, x.info_text, exdb.db_release, t.source
                        FROM (( (transcript t)
                          LEFT JOIN xref x ON x.xref_id = t.display_xref_id )
                          LEFT JOIN external_db exdb ON exdb.external_db_id = x.external_db_id )
                         WHERE t.stable_id = '%s' AND t.is_current = 1
                         ORDER BY t.transcript_id """, stableId);
    return connectionHandler
            .pool(connectionParams)
            .query(sql)
            .execute()
            .onItem()
            .transform(RowSet::iterator)
            .onItem()
            .transform(itr -> itr.hasNext() ? transcriptDto(itr.next()) : Transcript.newBuilder().build());
  }

  public Multi<Transcript> fetchAllByGeneId(ConnectionParams connectionParams, int geneId) {
    String sql = String.format("""
                        SELECT  t.transcript_id, t.seq_region_id, t.seq_region_start, t.seq_region_end, t.seq_region_strand, 
                         t.analysis_id, t.gene_id, t.is_current, t.stable_id, t.version, created_date, modified_date, t.description, 
                         t.biotype, exdb.db_name, exdb.status, exdb.db_display_name, x.xref_id, x.display_label, x.dbprimary_acc, 
                         x.version, x.description, x.info_type, x.info_text, exdb.db_release, t.source
                        FROM (( (transcript t)
                          LEFT JOIN xref x ON x.xref_id = t.display_xref_id )
                          LEFT JOIN external_db exdb ON exdb.external_db_id = x.external_db_id )
                         WHERE t.gene_id = %d AND t.is_current = 1
                         ORDER BY t.transcript_id """, geneId);
    return connectionHandler
            .pool(connectionParams)
            .query(sql)
            .execute()
            .onItem()
            .transformToMulti(rows -> Multi.createFrom().iterable(rows))
            .onItem()
            .transform(this::transcriptDto);
  }

  private Transcript transcriptDto(Row r) {
    Transcript.Builder transcriptBuilder = Transcript.newBuilder();

    if (Objects.isNull(r)) {
      return Transcript.newBuilder().build();
    }

    String xrefDisplayLabel = r.getString("display_label");
    if (StringUtils.isNotEmpty(xrefDisplayLabel)) {
      transcriptBuilder.setDisplayXref(mapDBEntry(r));
    }

    return transcriptBuilder
            .setDbId(protoDefaultValue(r.getInteger("gene_id"), iCls))
            .setSeqRegionId(protoDefaultValue(r.getInteger("seq_region_id"), iCls))
            .setStart(protoDefaultValue(r.getInteger("seq_region_start"), iCls))
            .setEnd(protoDefaultValue(r.getInteger("seq_region_end"), iCls))
            .setStrand(protoDefaultValue(r.getInteger("seq_region_strand"), iCls))
            .setAnalysisId(protoDefaultValue(r.getInteger("analysis_id"), iCls))
            .setBioType(protoDefaultValue(r.getString("biotype"), sCls))
            .setDisplayXrefId(protoDefaultValue(r.getInteger("xref_id"), iCls))
            .setSource(protoDefaultValue(r.getString("source"), sCls))
            .setDescription(protoDefaultValue(r.getString("description"), sCls))
            .setIsCurrent(protoDefaultValue(r.getBoolean("is_current"), bCls))
            .setStableId(protoDefaultValue(r.getString("stable_id"), sCls))
            .setVersion(protoDefaultValue(r.getInteger("version"), iCls))
            .setCreatedDate(protoDefaultValue(r.getLocalDateTime("created_date"), ldtCls).toString())
            .setModifiedDate(protoDefaultValue(r.getLocalDateTime("modified_date"), ldtCls).toString())
            .build();
  }

  private DBEntry mapDBEntry(Row r) {
    return DBEntry.newBuilder()
            .setDbId(protoDefaultValue(r.getInteger("xref_id"), iCls))
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
