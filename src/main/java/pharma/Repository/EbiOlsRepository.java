package pharma.Repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;

import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface EbiOlsRepository extends CrudRepository<AbstractTerm, Long> {

	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) "
			+ "AND dtype LIKE 'EbiOlsTerm'")
	List<AbstractTerm> findByIri(String iri);
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.synonym LIKE LOWER(CONCAT('%',:syn, '%')) "
			+ "AND dtype LIKE 'EbiOlsTerm'"
			+ "AND ontoclass LIKE UPPER(CONCAT('%',:oc, '%'))")
	List<AbstractTerm> findBySynonym(@Param("syn") String synonym, @Param("oc") String ontoClass);
		
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.parent = :parentId "
			+ "AND dtype LIKE 'EbiOlsTerm'"
			+ "AND ontoclass LIKE UPPER(CONCAT('%',:oc, '%'))")
	List<AbstractTerm> findByParent(@Param("parentId") AbstractTerm parent, @Param("oc") String ontoClass);	
}
