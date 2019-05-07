package pharma.Repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;

/**
 * Abtract repository with all the possible method types
 * Each different data source has its own repo class for the case
 * that an API or structure changes and there will be a different query necessary
 * @author asztrik
 *
 */
public interface AbstractRepository extends Neo4jRepository<AbstractTerm, Long> {

	/**
	 * Retrieves a term by Iri
	 * @param iri
	 * @return
	 */
	@Query("MATCH (t:AbstractTerm) WHERE t.iri CONTAINS {iri} RETURN t")
	List<AbstractTerm> findByIri(@Param("iri") String iri);
	
	/**
	 * Retrieves a term by synonym , searching only the synonym field
	 * @param synonym
	 * @return
	 */
	@Query("MATCH (t:AbstractTerm) WHERE lower(t.synonym) CONTAINS lower({synonym}) RETURN t")
	List<AbstractTerm> findBySynonym(@Param("synonym") String synonym);
	
	/**
	 * Retrieves a term by synonym and ontology class
	 * @param synonym
	 * @param className
	 * @return
	 */
	@Query("MATCH (t:AbstractTerm) WHERE lower(t.synonym) CONTAINS lower({synonym}) AND t.ontoclass CONTAINS {className} RETURN t")
	List<AbstractTerm> findBySynonym(@Param("synonym") String synonym, @Param("className") String className);	
	
	/**
	 * Retrieves a set of terms by one common parent
	 * @param parent
	 * @return
	 */
	@Query("MATCH (a:AbstractTerm)-[:CHILD_OF]->(b:AbstractTerm) WHERE b.iri CONTAINS {parentIri} RETURN a")
	List<AbstractTerm> findByParent(@Param("parentIri") String parentIri);
	
	/**
	 * Retrieves a set of terms by one common parent with filtering for ontology class
	 * @param parent
	 * @param className
	 * @return
	 */
	@Query("MATCH (a:AbstractTerm)-[:CHILD_OF]->(b:AbstractTerm) WHERE b.iri CONTAINS {parentIri} AND b.ontoclass CONTAINS {className} RETURN a")
	List<AbstractTerm> findByParent(@Param("parentIri") String parentIri, @Param("className") String className);	
}