package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.Exon;
import org.ebi.ensembl.grpc.common.Slice;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

import static org.ebi.ensembl.util.DbUtil.*;

@ApplicationScoped
public class ExonRepo {
  private final ConnectionHandler connectionHandler;

  public ExonRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

    public Multi<Exon> fetchAllBySlice(ConnectionParams connectionParams, Slice slice) {
// AND t.seq_region_start <= %d AND t.seq_region_end >= %d
        StringBuilder constraintSb =
                new StringBuilder(String.format(" e.is_current = 1 AND e.seq_region_id = %d ",
                        slice.getSeqRegionId(), slice.getStart(), slice.getEnd()));
        return connectionHandler
                .pool(connectionParams)
                .query("""
                            SELECT e.exon_id, e.seq_region_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand,
                            e.phase, e.end_phase, e.is_current, e.is_constitutive, e.stable_id, e.version, created_date, modified_date
                            FROM  (exon e) WHERE """ + constraintSb)
                .execute()
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem()
                .transform(this::exonDto);
    }

    public Uni<Exon> findByDbId(ConnectionParams connectionParams, int dbId) {
        String sql = String.format("""
                         SELECT  e.exon_id, e.seq_region_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand, 
                         e.phase, e.end_phase, e.is_current, e.is_constitutive, e.stable_id, e.version, created_date, modified_date
                        FROM  (exon e) WHERE e.exon_id = %d AND e.is_current = 1 """, dbId);
        return connectionHandler
                .pool(connectionParams)
                .query(sql)
                .execute()
                .onItem()
                .transform(RowSet::iterator)
                .onItem()
                .transform(itr -> itr.hasNext() ? exonDto(itr.next()) : Exon.newBuilder().build());
    }

    public Uni<Exon> findByStableId(ConnectionParams connectionParams, String stableId) {
        String sql = String.format("""
                         SELECT  e.exon_id, e.seq_region_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand, 
                         e.phase, e.end_phase, e.is_current, e.is_constitutive, e.stable_id, e.version, created_date, modified_date
                        FROM  (exon e) WHERE e.stable_id = '%s' AND e.is_current = 1 """, stableId);
        return connectionHandler
                .pool(connectionParams)
                .query(sql)
                .execute()
                .onItem()
                .transform(RowSet::iterator)
                .onItem()
                .transform(itr -> itr.hasNext() ? exonDto(itr.next()) : Exon.newBuilder().build());
    }

    public Multi<Exon> fetchAllByTranscriptId(ConnectionParams connectionParams, int transcriptId) {
        String sql = String.format("""
                         SELECT  e.exon_id, e.seq_region_id, e.seq_region_start, e.seq_region_end, e.seq_region_strand, e.phase, e.end_phase, 
                         e.is_current, e.is_constitutive, e.stable_id, e.version, created_date, modified_date FROM  (exon e, exon_transcript et) 
                         WHERE et.transcript_id = %d AND e.exon_id = et.exon_id ORDER BY et.transcript_id, et.rank """, transcriptId);
        return connectionHandler
                .pool(connectionParams)
                .query(sql)
                .execute()
                .onItem()
                .transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem()
                .transform(this::exonDto);
    }

    private Exon exonDto(Row r) {
        Exon.Builder exonBuilder = Exon.newBuilder();

        if (Objects.isNull(r)) {
            return Exon.newBuilder().build();
        }

        return exonBuilder
                .setExonId(protoDefaultValue(r.getInteger("exon_id"), iCls))
                .setSeqRegionId(protoDefaultValue(r.getInteger("seq_region_id"), iCls))
                .setSeqRegionStart(protoDefaultValue(r.getInteger("seq_region_start"), iCls))
                .setSeqRegionEnd(protoDefaultValue(r.getInteger("seq_region_end"), iCls))
                .setSeqRegionStrand(protoDefaultValue(r.getInteger("seq_region_strand"), iCls))
                .setPhase(protoDefaultValue(r.getInteger("phase"), iCls))
                .setEndPhase(protoDefaultValue(r.getInteger("end_phase"), iCls))
                .setIsCurrent(protoDefaultValue(r.getBoolean("is_current"), bCls))
                .setIsConstitutive(protoDefaultValue(r.getBoolean("is_constitutive"), bCls))
                .setStableId(protoDefaultValue(r.getString("stable_id"), sCls))
                .setVersion(protoDefaultValue(r.getInteger("version"), iCls))
                .setCreatedDate(protoDefaultValue(r.getLocalDateTime("created_date"), ldtCls).toString())
                .setModifiedDate(protoDefaultValue(r.getLocalDateTime("modified_date"), ldtCls).toString())
                .build();
    }
}
