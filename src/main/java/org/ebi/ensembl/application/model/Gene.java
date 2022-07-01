package org.ebi.ensembl.application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gene {
  // DB Identifier
  @JsonProperty(value = "dbId", required = true)
  private Integer dbId;

  // The stable identifier of this gene
  @JsonProperty(value = "stableId", required = true)
  private String stableId;

  // the version of the stable identifier of this gene
  @JsonProperty(value = "version", required = true)
  private Integer stableIdVersion;

  private Integer seqRegionId;

  // Start position of the gene
  @JsonProperty("start")
  private Integer startPos;

  // end position of the gene
  @JsonProperty("end")
  private Integer endPos;

  // The strand the gene is on
  private Integer strand;

  // the biotype e.g. "protein_coding"
  private String bioType;

  // The genes description
  private String description;

  // the genes source, e.g. "ensembl"
  private String source;

  private Integer analysisId;

  // specifies if this is the current version of the gene
  @JsonProperty(value = "isCurrent", required = true)
  private Boolean isCurrent;

  // the slice the gene is on
  private Slice slice;

  private Integer displayXrefId;

  // the external database name associated with this gene
  private String externalName;

  // the name of the database the external name is from
  private String externalDb;

  // the status of the external identifier
  private String externalStatus;

  // The external database entry that is used to label this gene when it is displayed.
  private DBEntry displayXref;

  // this gene's transcripts
  private List<Transcript> transcripts;

  // the canonical transcript dbID of this gene, if the transcript object itself is not available.
  private Integer canonicalTranscriptId;

  // the date the gene was created
  private LocalDateTime createdDate;

  // the date the gene was last modified
  private LocalDateTime modifiedDate;
}
