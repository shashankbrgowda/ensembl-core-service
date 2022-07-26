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
        .transform(itr -> itr.hasNext() ? mapAnalysis(itr.next()) : null);
  }

  private Analysis mapAnalysis(Row r) {
    Analysis.Builder analysisBuilder = Analysis.newBuilder();

    if (Objects.isNull(r)) {
      return null;
    }

    if (Objects.nonNull(r.getInteger("analysis_id"))) {
      analysisBuilder.setAnalysisId(r.getInteger("analysis_id"));
    }

    if (Objects.nonNull(r.getString("logic_name"))) {
      analysisBuilder.setLogicName(r.getString("logic_name"));
    }

    if (Objects.nonNull(r.getString("program"))) {
      analysisBuilder.setProgram(r.getString("program"));
    }

    if (Objects.nonNull(r.getString("program_version"))) {
      analysisBuilder.setProgramVersion(r.getString("program_version"));
    }

    if (Objects.nonNull(r.getString("program_file"))) {
      analysisBuilder.setProgramFile(r.getString("program_file"));
    }

    if (Objects.nonNull(r.getString("db"))) {
      analysisBuilder.setDb(r.getString("db"));
    }

    if (Objects.nonNull(r.getString("db_version"))) {
      analysisBuilder.setDbVersion(r.getString("db_version"));
    }

    if (Objects.nonNull(r.getString("db_file"))) {
      analysisBuilder.setDbFile(r.getString("db_file"));
    }

    if (Objects.nonNull(r.getString("module"))) {
      analysisBuilder.setModule(r.getString("module"));
    }

    if (Objects.nonNull(r.getString("module_version"))) {
      analysisBuilder.setModuleVersion(r.getString("module_version"));
    }

    if (Objects.nonNull(r.getString("gff_source"))) {
      analysisBuilder.setGffSource(r.getString("gff_source"));
    }

    if (Objects.nonNull(r.getString("gff_feature"))) {
      analysisBuilder.setGffFeature(r.getString("gff_feature"));
    }

    if (Objects.nonNull(r.getLocalDateTime("created"))) {
      analysisBuilder.setCreated(r.getLocalDateTime("created").toString());
    }

    if (Objects.nonNull(r.getString("parameters"))) {
      analysisBuilder.setParameters(r.getString("parameters"));
    }

    if (Objects.nonNull(r.getString("description"))) {
      analysisBuilder.setDescription(r.getString("description"));
    }

    if (Objects.nonNull(r.getString("display_label"))) {
      analysisBuilder.setDisplayLabel(r.getString("display_label"));
    }

    if (Objects.nonNull(r.getInteger("displayable"))) {
      analysisBuilder.setDisplayable(r.getInteger("displayable"));
    }

    if (Objects.nonNull(r.getString("web_data"))) {
      analysisBuilder.setWebData(r.getString("web_data"));
    }

    return analysisBuilder.build();
  }
}
