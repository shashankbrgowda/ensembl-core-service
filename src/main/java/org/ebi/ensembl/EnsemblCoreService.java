package org.ebi.ensembl;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@QuarkusMain
@ApplicationPath("api")
@OpenAPIDefinition(
    info =
        @Info(
            title = "Ensembl Core Service",
            version = "1.0.0",
            contact = @Contact(name = "shabr@ebi.ac.uk", email = "shabr@ebi.ac.uk"),
            description =
                "Ensembl Core Service is the new gRPC layer equivalent of Ensembl Perl API, It exposes perl adaptors as gRPC endpoints",
            license =
                @License(
                    name = "Apache 2.0",
                    url = "https://www.apache.org/licenses/LICENSE-2.0.html")))
public class EnsemblCoreService extends Application {
  public static void main(String[] args) {
    Quarkus.run(args);
  }
}
