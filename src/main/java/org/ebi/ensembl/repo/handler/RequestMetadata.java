package org.ebi.ensembl.repo.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMetadata {
    private String species;
    private String group;
    private String type;
}
