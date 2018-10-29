package pharma.Repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import pharma.Term.AbstractTerm;

import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface EbiOlsRepository extends CrudRepository<AbstractTerm, Long> {

	@Query("SELECT p FROM AbstractTerm p WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) AND dtype LIKE 'EbiOlsTerm'")
	List<AbstractTerm> findByIri(String iri);
	
	@Query("SELECT p FROM AbstractTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%')) AND dtype LIKE 'EbiOlsTerm'")
	List<AbstractTerm> findBySynonym(String synonym);
	
	@Query("SELECT p FROM AbstractTerm p WHERE p.parent = parent AND dtype LIKE 'EbiOlsTerm'")
	List<AbstractTerm> findByParent(Integer parent);	
}
