package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.grpc.common.Slice;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

import static org.ebi.ensembl.util.DbUtil.*;

// TODO: Error handling
@ApplicationScoped
public class SequenceRegionRepo {
  private final ConnectionHandler connectionHandler;

  public SequenceRegionRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Multi<Slice> genericFetch(ConnectionParams connectionParams, String sql) {
    return connectionHandler
        .pool(connectionParams)
        .query(sql)
        .execute()
        .onItem()
        .transformToMulti(set -> Multi.createFrom().iterable(set))
        .onItem()
        .transform(this::mapSlice);
  }

  public Uni<Slice> fetchSeqRegionByNameAndCoordSysId(
      ConnectionParams connectionParams, String seqRegionName, Integer coordSysId) {
    return connectionHandler
        .pool(connectionParams)
        .query(
            String.format(
                "SELECT seq_region_id, name, length FROM seq_region WHERE coord_system_id = %d AND name = '%s'",
                coordSysId, seqRegionName))
        .execute()
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? mapSeqRegion(itr.next()) : null);
  }

  public Uni<Slice> fetchBySeqRegionId(ConnectionParams connectionParams, Integer seqRegionId) {
    return connectionHandler
        .pool(connectionParams)
        .query(
            String.format("""
                    SELECT sr.seq_region_id, sr.name as sr_name, sr.length, sr.coord_system_id, cs.name as cs_name,
                    cs.rank, cs.version, cs.attrib FROM seq_region sr, coord_system cs WHERE sr.coord_system_id = cs.coord_system_id
                    AND sr.seq_region_id = %d""",
                seqRegionId))
        .execute()
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? mapSlice(itr.next()) : null);
  }

  private Slice mapSeqRegion(Row r) {
    if (Objects.isNull(r)) {
      return null;
    }

    Integer len = protoDefaultValue(r.getInteger("length"), iCls);
    // TODO: check is this is correct because for one of slice start is not 1
    // and end is not related to length ( ex seq_region_id: 131553, length: 57227415)
    return Slice.newBuilder()
            .setSeqRegionId(protoDefaultValue(r.getInteger("seq_region_id"), iCls))
            .setSeqRegionName(protoDefaultValue(r.getString("name"), sCls))
            .setSeqRegionLength(len)
            .setEnd(len)
            .setStart(1)
            .setStrand(1)
            .build();
  }

  // TODO: Remove coord system here, Should be just sequence region related info
  private Slice mapSlice(Row r) {
    if (Objects.isNull(r)) {
      return null;
    }

    CoordSystem.Builder coordSysBuilder = CoordSystem.newBuilder();
    if (Objects.nonNull(r.getString("attrib"))) {
      String[] attribs = r.getString("attrib").split(",");
      for (String attrib : attribs) {
        if (Objects.equals(attrib, "sequence_level")) {
          coordSysBuilder.setSequenceLevel(1);
        }
        if (Objects.equals(attrib, "default_version")) {
          coordSysBuilder.setDefault(1);
        }
      }
    }

    CoordSystem coordSystem = coordSysBuilder
            .setDbId(protoDefaultValue(r.getInteger("coord_system_id"), iCls))
            .setName(protoDefaultValue(r.getString("cs_name"), sCls))
            .setRank(protoDefaultValue(r.getInteger("rank"), iCls))
            .setVersion(protoDefaultValue(r.getString("version"), sCls))
            .build();

    Integer len = protoDefaultValue(r.getInteger("length"), iCls);
    return Slice.newBuilder()
            .setSeqRegionId(protoDefaultValue(r.getInteger("seq_region_id"), iCls))
            .setSeqRegionName(protoDefaultValue(r.getString("sr_name"), sCls))
            .setSeqRegionLength(len)
            .setEnd(len)
            .setStart(1)
            .setStrand(1)
            .setCoordSystem(coordSystem).build();
  }
}
