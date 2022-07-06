package org.ebi.ensembl.domain.service.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.ebi.ensembl.application.model.Analysis;
import org.ebi.ensembl.application.model.GeneObj;
import org.ebi.ensembl.application.model.Slice;
import org.ebi.ensembl.domain.service.CoreService;
import org.ebi.ensembl.grpc.Gene;
import org.ebi.ensembl.infra.repo.CoreRepo;
import org.ebi.ensembl.infra.repo.handler.ConnectionParams;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

/*
TODO: fetch_all_by_outward_search, fetch_all_by_logic_name, fetch_all_by_Slice_and_score,
 fetch_all_by_Slice_constraint, fetch_all_nearest_by_Feature, fetch_Iterator_by_Slice,
 fetch_Iterator_by_Slice_method, fetch_nearest_by_Feature, generate_in_constraint
 */
@ApplicationScoped
public class GeneService implements CoreService<Gene> {
  private final CoreRepo<Gene> geneCoreRepo;

  public GeneService(CoreRepo<Gene> geneCoreAdaptor) {
    this.geneCoreRepo = geneCoreAdaptor;
  }

  /**
   * Retrieves count of gene objects from the database via its biotype or biotypes.
   *
   * @param params Db connection parameters
   * @param bioTypes The biotype of the gene to retrieve.
   * @return Count of genes with a given biotype
   */
  public Integer countAllByBioTypes(ConnectionParams params, Set<String> bioTypes) {
    return null;
  }

  /**
   * Method to count genes on a given slice, filtering by biotype and source
   *
   * @param params Db connection parameters
   * @param slice The slice to count genes on.
   * @param bioType (Optional) the biotype of the features to count.
   * @param source (Optional) the source name of the features to count.
   * @return
   */
  public Integer countAllBySlice(
      ConnectionParams params, Slice slice, String bioType, String source) {
    return null;
  }

  /**
   * Retrieves count of gene objects from the database via its source or sources.
   *
   * @param params Db connection parameters
   * @param sources The source of the gene to retrieve.
   * @return
   */
  public Integer countAllBySources(ConnectionParams params, Set<String> sources) {
    return null;
  }

  /**
   * Retrieves genes which are alternate alleles to a provided gene. Alternate alleles in Ensembl
   * are genes which are similar and are on an alternative haplotype of the same region. There are
   * not currently very many of these. This method will return a reference to an empty list if no
   * alternative alleles are found.
   *
   * @param params Db connection parameters
   * @param geneObj The gene to fetch alternative alleles for
   * @param warnGeneWithoutAltAllele Ask the method to warn about any gene without an alt allele
   *     group.
   * @return
   */
  public Multi<GeneObj> fetchAllAltAlleles(
      ConnectionParams params, GeneObj geneObj, Boolean warnGeneWithoutAltAllele) {
    return null;
  }

  /**
   * Retrieves an array reference of gene objects from the database via its biotype or biotypes. The
   * genes will be retrieved in its native coordinate system (i.e. in the coordinate system it is
   * stored in the database). It may be converted to a different coordinate system through a call to
   * transform() or transfer(). If the gene or exon is not found undef is returned instead.
   *
   * @param params Db connection parameters
   * @param bioTypes The biotype of the gene to retrieve.
   * @return
   */
  public Multi<GeneObj> fetchAllByBioTypes(ConnectionParams params, Set<String> bioTypes) {
    return null;
  }

  /**
   * TODO: BASE ADAPTOR Returns the features created from the database defined by the the IDs in
   * contained in the provided ID list $id_list. The features will be returned in their native
   * coordinate system. That is, the coordinate system in which they are stored in the database. In
   * order to convert the features to a particular coordinate system use the transfer() or
   * transform() method. If none of the features are found in the database a reference to an empty
   * list is returned.
   *
   * @param params Db connection parameters
   * @param dbIds The unique database identifiers for the features to be obtained.
   * @param slice (Optional) Slice to map features onto.
   * @return
   */
  public Multi<GeneObj> fetchAllByDbIdList(
      ConnectionParams params, Set<Integer> dbIds, Slice slice) {
    return null;
  }

  /**
   * Fetches genes by their textual description. Fully supports SQL wildcards, since getting an
   * exact hit is unlikely.
   *
   * @param connectionParams Db connection parameters
   * @param description String of description
   * @return
   */
  public Multi<GeneObj> fetchAllByDescription(
      ConnectionParams connectionParams, String description) {
    return null;
  }

  /**
   * Returns all genes which have the given display label or undef if there are none.
   *
   * @param params Db connection parameters
   * @param displayLabel display label of genes to fetch
   * @return
   */
  public Multi<GeneObj> fetchAllByDisplayLabel(ConnectionParams params, String displayLabel) {
    return null;
  }

  /**
   * Gets all the genes with transcripts with exons which have a specified hit on a particular type
   * of feature. Optionally filter by analysis.
   *
   * @param connectionParams Db connection parameters
   * @param hitName Name of supporting feature
   * @param featureType one of "dna_align_feature" or "protein_align_feature"
   * @param analysis (optional) Analysis
   * @return
   */
  public Multi<GeneObj> fetchAllByExonSupportingEvidence(
      ConnectionParams connectionParams, String hitName, String featureType, Analysis analysis) {
    return null;
  }

  /**
   * Retrieves a list of genes with an external database identifier externalName. The genes returned
   * are in their native coordinate system, i.e. in the coordinate system they are stored in the
   * database in. If another coordinate system is required then the Gene::transfer or
   * Gene::transform method can be used. SQL wildcards % and _ are supported in the externalName,
   * but their use is somewhat restricted for performance reasons. Users that really do want % and _
   * in the first three characters should use argument 4 (sqlOverride) to prevent optimisations
   *
   * @param params Db connection parameters
   * @param externalName The external identifier for the gene to be obtained
   * @param externalDbName (optional) The name of the external database from which the identifier
   *     originates.
   * @param sqlOverride Boolean override. Force SQL regex matching for users who really do want to
   *     find all 'NM%'
   * @return
   */
  public Multi<GeneObj> fetchAllByExternalName(
      ConnectionParams params, String externalName, String externalDbName, Boolean sqlOverride) {
    return null;
  }

  /**
   * Retrieves a list of genes that are associated with the given ontology linkage type. The genes
   * returned are in their native coordinate system, i.e. in the coordinate system in which they are
   * stored in the database.
   *
   * <p>Perl api ex: <code>
   *     my $genes = $gene_adaptor->fetch_all_by_ontology_linkage_type('GO', 'IMP');
   *     my $genes = $gene_adaptor->fetch_all_by_ontology_linkage_type(undef, 'IMP');
   * </code>
   *
   * @param params Db connection parameters
   * @param dbName (optional) The database name to search for. Defaults to GO
   * @param linkageType Linkage type to search for e.g. IMP
   * @return
   */
  public Multi<GeneObj> fetchAllByOntologyLinkageType(
      ConnectionParams params, String dbName, String linkageType) {
    return null;
  }

  /**
   * Overrides superclass method to optionally load transcripts immediately rather than lazy-loading
   * them later. This is more efficient when there are a lot of genes whose transcripts are going to
   * be used.
   *
   * @param params Db connection parameters
   * @param slice The slice to fetch genes on.
   * @param logicName (optional) the logic name of the type of features to obtain
   * @param loadTranscripts (optional) if true, transcripts will be loaded immediately rather than
   *     lazy loaded later.
   * @param source (optional) the source name of the features to obtain.
   * @param bioType (optional) the biotype of the features to obtain.
   * @return
   */
  public Multi<GeneObj> fetchAllBySlice(
      ConnectionParams params,
      Slice slice,
      String logicName,
      Boolean loadTranscripts,
      String source,
      String bioType) {
    return null;
  }

  /**
   * Overrides superclass method to optionally load transcripts immediately rather than lazy-loading
   * them later. This is more efficient when there are a lot of genes whose transcripts are going to
   * be used. The genes are then filtered to return only those with external database links of the
   * type specified
   *
   * <p>Perl api ex: <code>
   *     genes = @{ $ga->fetch_all_by_Slice_and_external_dbname_link( $slice, undef, undef, "HGNC" ) };
   * </code>
   *
   * @param params Db connection parameters
   * @param slice The slice to fetch genes on.
   * @param logicName (optional) the logic name of the type of features to obtain
   * @param loadTranscripts (optional) if true, transcripts will be loaded immediately rather than
   *     lazy loaded later.
   * @param externalDbName Name of the external database to fetch the Genes by
   * @return
   */
  public Multi<GeneObj> fetchAllBySliceAndExternalDbNameLink(
      ConnectionParams params,
      Slice slice,
      String logicName,
      Boolean loadTranscripts,
      String externalDbName) {
    return null;
  }

  /**
   * Retrieves an array reference of gene objects from the database via its source or sources. The
   * gene will be retrieved in its native coordinate system (i.e. in the coordinate system it is
   * stored in the database). It may be converted to a different coordinate system through a call to
   * transform() or transfer(). If the gene or exon is not found undef is returned instead.
   *
   * <p>Perl api ex: <code>
   *      $genes = $gene_adaptor->fetch_all_by_source(['ensembl', 'vega']);
   * </code>
   *
   * @param params Db connection parameters
   * @param sources The source of the gene to retrieve. You can have as an argument a reference to a
   *     list of sources
   * @return
   */
  public Multi<GeneObj> fetchAllBySources(ConnectionParams params, Set<String> sources) {
    return null;
  }

  /**
   * Returns a listref of features identified by their stable IDs. This method only fetches features
   * of the same type as the calling adaptor. Results are constrained to a slice if the slice is
   * provided.
   *
   * <p>Perl api ex: <code>
   *     $fs = $a->fetch_all_by_stable_id_list(["ENSG00001","ENSG00002", ...]);
   * </code>
   *
   * @param params Db connection parameters
   * @param stableIds Stable ID list
   * @param slice The slice from which to obtain features
   * @return
   */
  public Multi<GeneObj> fetchAllByStableIdList(
      ConnectionParams params, Set<String> stableIds, Slice slice) {
    return null;
  }

  /**
   * Gets all the genes with transcripts with evidence for a specified hit on a particular type of
   * feature. Optionally filter by analysis.
   *
   * @param params Db connection parameters
   * @param hitName Name of supporting feature
   * @param featureType One of "dna_align_feature" or "protein_align_feature"
   * @param analysis (optional) Analysis
   * @return
   */
  public Multi<GeneObj> fetchAllByTranscriptSupportingEvidence(
      ConnectionParams params, String hitName, String featureType, Analysis analysis) {
    return null;
  }

  /**
   * Similar to fetch_by_stable_id, but retrieves all versions of a gene stored in the database.
   *
   * @param params Db connection parameters
   * @param stableId The stable ID of the gene to retrieve
   * @return
   */
  public Multi<GeneObj> fetchAllVersionsByStableId(ConnectionParams params, String stableId) {
    return null;
  }

  /**
   * Returns the feature created from the database defined by the the id $id. The feature will be
   * returned in its native coordinate system. That is, the coordinate system in which it is stored
   * in the database. In order to convert it to a particular coordinate system use the transfer() or
   * transform() method. If the feature is not found in the database then undef is returned instead
   *
   * @param params Db connection parameters
   * @param dbId The unique database identifier for the feature to be obtained
   * @return
   */
  @Override
  public Uni<Gene> fetchByDbId(ConnectionParams params, Integer dbId) {
    return null;
  }

  /**
   * Returns the gene which has the given display label or undef if there is none. If there are more
   * than 1, the gene on the reference slice is reported or if none are on the reference, the first
   * one is reported.
   *
   * @param params Db connection parameters
   * @param label display label of gene to fetch
   * @return
   */
  public Uni<GeneObj> fetchByDisplayLabel(ConnectionParams params, String label) {
    return null;
  }

  /**
   * Retrieves a gene object from the database via an exon stable id. The gene will be retrieved in
   * its native coordinate system (i.e. in the coordinate system it is stored in the database). It
   * may be converted to a different coordinate system through a call to transform() or transfer().
   * If the gene or exon is not found undef is returned instead.
   *
   * @param params Db connection parameters
   * @param exonStableId The stable id of an exon of the gene to retrieve
   * @return
   */
  public Uni<GeneObj> fetchByExonStableId(ConnectionParams params, String exonStableId) {
    return null;
  }

  /**
   * Retrieves a gene object from the database via its stable id. The gene will be retrieved in its
   * native coordinate system (i.e. in the coordinate system it is stored in the database). It may
   * be converted to a different coordinate system through a call to transform() or transfer(). If
   * the gene or exon is not found undef is returned instead.
   *
   * @param params Db connection parameters
   * @param stableId The stable ID of the gene to retrieve
   * @return
   */
  public Uni<GeneObj> fetchByStableId(ConnectionParams params, String stableId) {
    return null;
  }

  /**
   * Retrieves a gene object from the database via its stable id and version. The gene will be
   * retrieved in its native coordinate system (i.e. in the coordinate system it is stored in the
   * database). It may be converted to a different coordinate system through a call to transform()
   * or transfer(). If the gene or exon is not found undef is returned instead.
   *
   * @param params Db connection parameters
   * @param stableId The stable ID of the gene to retrieve
   * @param version The version of the stable_id to retrieve
   * @return
   */
  public Uni<GeneObj> fetchByStableIdVersion(
      ConnectionParams params, String stableId, Integer version) {
    return null;
  }

  /**
   * Retrieves a gene from the database via the database identifier of one of its transcripts.
   *
   * @param params Db connection parameters
   * @param transcriptId Unique database identifier for the transcript whose gene should be
   *     retrieved. The gene is returned in its native coord system (i.e. the coord_system it is
   *     stored in). If the coord system needs to be changed, then tranform or transfer should be
   *     called on the returned object. undef is returned if the gene or transcript is not found in
   *     the database.
   * @return
   */
  public Uni<GeneObj> fetchByTranscriptId(ConnectionParams params, String transcriptId) {
    return null;
  }

  /**
   * Retrieves a gene from the database via the stable ID of one of its transcripts
   *
   * @param params Db connection parameters
   * @param transcriptStableId transcript stable ID whose gene should be retrieved
   * @return
   */
  public Uni<GeneObj> fetchByTranscriptStableId(
      ConnectionParams params, String transcriptStableId) {
    return null;
  }

  /**
   * Retrieves a gene via the stable id of one of its translations.
   *
   * @param params Db connection parameters
   * @param translationStableId The stable id of a translation of the gene to be obtained
   * @return
   */
  public Uni<GeneObj> fetchByTranslationStableId(ConnectionParams params, String translationStableId) {
    return null;
  }

  /**
   * Stores a gene in the database.
   *
   * @param params Db connection parameters
   * @param geneObj The gene to store in the database
   * @param ignoreReleaseInXref ignore_release in xrefs [default 1] set to 0 to use release info in
   *     external database references
   * @param preventCoOrdReCalc prevent coordinate recalculation if you are persisting transcripts
   *     with this gene
   * @param preventSupportFeaturesCopy prevent copying supporting features across exons increased
   *     speed for lost accuracy
   * @return the database identifier (dbID) of the newly stored gene
   */
  public Uni<Integer> store(
      ConnectionParams params,
      GeneObj geneObj,
      Boolean ignoreReleaseInXref,
      Boolean preventCoOrdReCalc,
      Boolean preventSupportFeaturesCopy) {
    return null;
  }


}
