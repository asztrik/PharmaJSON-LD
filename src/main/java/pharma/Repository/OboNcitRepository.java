package pharma.Repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;




public interface OboNcitRepository extends CrudRepository<AbstractTerm, Long> {
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) "
			+ "AND dtype LIKE 'OboNcitTerm'")
	List<AbstractTerm> findByIri(String iri);
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.synonym LIKE LOWER(CONCAT('%',:syn, '%')) "
			+ "AND dtype LIKE 'OboNcitTerm'"
			+ "AND ontoclass LIKE UPPER(CONCAT('%',:oc, '%'))")
	List<AbstractTerm> findBySynonym(@Param("syn") String synonym, @Param("oc") String ontoClass);
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.parent = :parentId "
			+ "AND dtype LIKE 'OboNcitTerm'"
			+ "AND ontoclass LIKE UPPER(CONCAT('%',:oc, '%'))")
	List<AbstractTerm> findByParent(@Param("parentId") AbstractTerm parent, @Param("oc") String ontoClass);	
}
