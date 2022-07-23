package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.sqlclient.Row;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.grpc.common.CoordSystem;
import org.ebi.ensembl.grpc.common.Slice;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

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

    private Slice  mapSlice(Row r) {
        Slice.Builder sliceBuilder = Slice.newBuilder();
        CoordSystem.Builder coordSysBuilder = CoordSystem.newBuilder();

        if (Objects.isNull(r)) {
            return null;
        }

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

        if (Objects.nonNull(r.getInteger("coord_system_id"))) {
            coordSysBuilder.setDbId(r.getInteger("coord_system_id"));
        }

        if (Objects.nonNull(r.getString("cs_name"))) {
            coordSysBuilder.setName(r.getString("cs_name"));
        }

        if (Objects.nonNull(r.getInteger("rank"))) {
            coordSysBuilder.setRank(r.getInteger("rank"));
        }

        if (Objects.nonNull(r.getString("version"))) {
            coordSysBuilder.setVersion(r.getString("version"));
        }

        if (Objects.nonNull(r.getInteger("seq_region_id"))) {
            sliceBuilder.setSeqRegionId(r.getInteger("seq_region_id"));
        }

        if (Objects.nonNull(r.getString("sr_name"))) {
            sliceBuilder.setSeqRegionName(r.getString("sr_name"));
        }

        Integer sliceLen = r.getInteger("length");
        if (Objects.nonNull(sliceLen)) {
            sliceBuilder.setSeqRegionLength(sliceLen);
        }

        // TODO: check is this is correct because for one of slice start is not 1
        // and end is not related to length ( ex seq_region_id: 131553, length: 57227415)
        sliceBuilder.setStart(1);
        sliceBuilder.setEnd(sliceLen);
        sliceBuilder.setStrand(1);
        sliceBuilder.setCoordSystem(coordSysBuilder.build());

        return sliceBuilder.build();
    }
}
