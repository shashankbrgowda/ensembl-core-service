package org.ebi.ensembl.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.grpc.slice.FetchAllSliceRequest;
import org.ebi.ensembl.grpc.slice.FetchAllSliceResponse;
import org.ebi.ensembl.grpc.slice.SliceSvc;

@GrpcService
public class SliceAdaptor implements SliceSvc {
    @Override
    public Uni<FetchAllSliceResponse> fetchAll(FetchAllSliceRequest request) {
        return null;
    }
}
