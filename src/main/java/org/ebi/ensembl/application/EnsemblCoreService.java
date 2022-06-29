package org.ebi.ensembl.application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
@OpenAPIDefinition(
    tags = {
      @Tag(name = "ensembl core", description = "Adaptors to fetch data from core databases."),
      @Tag(name = "gasket", description = "Operations related to gaskets")
    },
    info =
        @Info(
            title = "Ensembl Core API Service",
            version = "1.0.0",
            contact = @Contact(name = "Ensembl Infrastructure Team", email = "shabr@ebi.ac.uk"),
            license =
                @License(
                    name = "Apache 2.0",
                    url = "https://www.apache.org/licenses/LICENSE-2.0.html")))
public class EnsemblCoreService extends Application {}
