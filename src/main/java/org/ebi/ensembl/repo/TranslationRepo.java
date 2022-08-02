package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.Translation;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

import static org.ebi.ensembl.util.DbUtil.*;

@ApplicationScoped
public class TranslationRepo {
  private final ConnectionHandler connectionHandler;

  public TranslationRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

    public Uni<Translation> fetchByDbId(ConnectionParams connectionParams, int dbId) {
      String sql = String.format("""
                        SELECT translation_id, transcript_id, seq_start, start_exon_id, seq_end,end_exon_id, stable_id, version,
                        created_date,modified_date FROM translation WHERE translation_id = %d """, dbId);
      return connectionHandler
              .pool(connectionParams)
              .query(sql)
              .execute()
              .onItem()
              .transform(RowSet::iterator)
              .onItem()
              .transform(itr -> itr.hasNext() ? translationDto(itr.next()) : Translation.newBuilder().build());
    }

  public Uni<Translation> fetchByStableId(ConnectionParams connectionParams, String stableId) {
    String sql = String.format("""
                        SELECT translation_id, transcript_id, seq_start, start_exon_id, seq_end,end_exon_id, stable_id, version,
                        created_date,modified_date FROM translation WHERE stable_id = '%s' """, stableId);
    return connectionHandler
            .pool(connectionParams)
            .query(sql)
            .execute()
            .onItem()
            .transform(RowSet::iterator)
            .onItem()
            .transform(itr -> itr.hasNext() ? translationDto(itr.next()) : Translation.newBuilder().build());
  }

  public Uni<Translation> fetchByTranscriptId(ConnectionParams connectionParams, int transcriptId) {
    String sql = String.format("""
                        SELECT translation_id, transcript_id, seq_start, start_exon_id, seq_end,end_exon_id, stable_id, version,
                        created_date,modified_date FROM translation WHERE transcript_id = %d """, transcriptId);
    return connectionHandler
            .pool(connectionParams)
            .query(sql)
            .execute()
            .onItem()
            .transform(RowSet::iterator)
            .onItem()
            .transform(itr -> itr.hasNext() ? translationDto(itr.next()) : Translation.newBuilder().build());
  }

  private Translation translationDto(Row r) {
    Translation.Builder translationBuilder = Translation.newBuilder();

    if( Objects.isNull(r)) {
      return Translation.newBuilder().build();
    }

    return translationBuilder
            .setTranslationId(protoDefaultValue(r.getInteger("translation_id"), iCls))
            .setTranscriptId(protoDefaultValue(r.getInteger("transcript_id"), iCls))
            .setSeqStart(protoDefaultValue(r.getInteger("seq_start"), iCls))
            .setSeqEnd(protoDefaultValue(r.getInteger("seq_end"), iCls))
            .setStartExonId(protoDefaultValue(r.getInteger("start_exon_id"), iCls))
            .setEndExonId(protoDefaultValue(r.getInteger("end_exon_id"), iCls))
            .setStableId(protoDefaultValue(r.getString("stable_id"), sCls))
            .setVersion(protoDefaultValue(r.getInteger("version"), iCls))
            .setCreatedDate(protoDefaultValue(r.getLocalDateTime("created_date"), ldtCls).toString())
            .setModifiedDate(protoDefaultValue(r.getLocalDateTime("modified_date"), ldtCls).toString())
            .build();
  }
}
