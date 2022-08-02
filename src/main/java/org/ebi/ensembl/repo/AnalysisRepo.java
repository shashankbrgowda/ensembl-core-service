package org.ebi.ensembl.repo;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.ebi.ensembl.grpc.common.Analysis;
import org.ebi.ensembl.grpc.common.ConnectionParams;
import org.ebi.ensembl.handler.ConnectionHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

import static org.ebi.ensembl.util.DbUtil.*;

@ApplicationScoped
public class AnalysisRepo {
  private final ConnectionHandler connectionHandler;

  public AnalysisRepo(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
  }

  public Uni<Analysis> fetchByDbId(ConnectionParams connectionParams, int analysisId) {
    return connectionHandler
        .pool(connectionParams)
        .preparedQuery(
                """
                     SELECT analysis.analysis_id, logic_name, program, program_version, program_file, db, db_version, 
                     db_file, module, module_version, gff_source, gff_feature, created, parameters, description, 
                     display_label, displayable, web_data
                     FROM analysis
                     LEFT JOIN analysis_description
                     ON analysis.analysis_id = analysis_description.analysis_id
                     WHERE analysis.analysis_id = ? """)
        .execute(Tuple.of(analysisId))
        .onItem()
        .transform(RowSet::iterator)
        .onItem()
        .transform(itr -> itr.hasNext() ? mapAnalysis(itr.next()) : Analysis.newBuilder().build());
  }

  private Analysis mapAnalysis(Row r) {
    if (Objects.isNull(r)) {
      return Analysis.newBuilder().build();
    }

    return Analysis.newBuilder()
            .setAnalysisId(protoDefaultValue(r.getInteger("analysis_id"), iCls))
            .setLogicName(protoDefaultValue(r.getString("logic_name"), sCls))
            .setProgram(protoDefaultValue(r.getString("program"), sCls))
            .setProgramVersion(protoDefaultValue(r.getString("program_version"), sCls))
            .setProgramFile(protoDefaultValue(r.getString("program_file"), sCls))
            .setDb(protoDefaultValue(r.getString("db"), sCls))
            .setDbVersion(protoDefaultValue(r.getString("db_version"), sCls))
            .setDbFile(protoDefaultValue(r.getString("db_file"), sCls))
            .setModule(protoDefaultValue(r.getString("module"), sCls))
            .setModuleVersion(protoDefaultValue(r.getString("module_version"), sCls))
            .setGffSource(protoDefaultValue(r.getString("gff_source"), sCls))
            .setGffFeature(protoDefaultValue(r.getString("gff_feature"), sCls))
            .setCreated(protoDefaultValue(r.getLocalDateTime("created"), ldtCls).toString())
            .setParameters(protoDefaultValue(r.getString("parameters"), sCls))
            .setDescription(protoDefaultValue(r.getString("description"), sCls))
            .setDisplayLabel(protoDefaultValue(r.getString("display_label"), sCls))
            .setDisplayable(protoDefaultValue(r.getInteger("displayable"), iCls))
            .setWebData(protoDefaultValue(r.getString("web_data"), sCls)).build();
  }
}
