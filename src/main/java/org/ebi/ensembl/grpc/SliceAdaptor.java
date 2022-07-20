package org.ebi.ensembl.grpc;

import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.slice.FetchAllRequest;
import org.ebi.ensembl.grpc.slice.FetchAllResponse;
import org.ebi.ensembl.grpc.slice.SliceSvc;
import org.ebi.ensembl.repo.handler.ConnectionParams;

public class SliceAdaptor implements SliceSvc {
    @Override
    public Uni<FetchAllResponse> fetchAll(FetchAllRequest request) {
        ConnectionParams connectionParams = ConnectionParams.mapConnectionParams(request.getParams());
        String coordSystemName = request.getCoordSystemName();

        return null;
    }
}
