package pharma.Repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ChebiRepository extends CrudRepository<AbstractTerm, Long> {

	@Query("SELECT p FROM AbstractTerm p "
			+ "WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) "
			+ "AND dtype LIKE 'ChebiTerm'")
	List<AbstractTerm> findByIri(String iri);
	
	@Query("SELECT p FROM AbstractTerm p "
			+ "WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%')) "
			+ "AND dtype LIKE 'ChebiTerm'")
	List<AbstractTerm> findBySynonym(String synonym);
		
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.parent = :parentId "
			+ "AND dtype LIKE 'ChebiTerm'")
	List<AbstractTerm> findByParent(@Param("parentId") AbstractTerm parent);	
}
