package org.ebi.ensembl.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** This object holds information about external references (xrefs) to Ensembl objects. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBEntry {
  // This is the object's primary id in the external database.
  private String primaryId;

  // The object's version in the external database.
  private String version;

  // The name of the external database.
  private String dbName;

  // The external database release name.
  private String release;

  // The object's preferred display name. This can be the same as primary_id or ensembl-specific.
  private String displayId;

  // The object's description, from the xref table
  private String description;

  // The external database priority.
  private String priority;

  //  The preferred display name for the external database. Has "Projected " prepended if
  // info_type='PROJECTION'.
  private String dbDisplayName;

  //  Defines the method by which the linked object was connected to this xref. Available types are:
  // 'CHECKSUM',
  //  'COORDINATE_OVERLAP', 'DEPENDENT', 'DIRECT', 'INFERRED_PAIR', 'MISC', 'NONE', 'PROBE',
  // 'PROJECTION', 'SEQUENCE_MATCH'
  //  AND 'UNMAPPED'.
  private String infoType;

  // Additional information recorded during the xref association. Intended to be used as
  // metadata.
  private String infoText;

  // The external database type.
  private String type;

  // The external database 'secondary' database name.
  private String secondaryDbName;

  // The external database 'secondary' database table.
  private String secondaryDbTable;

  // The object xref 'linkage annotation'.
  private String linkageAnnotation;
}
