package pharma.Repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import pharma.Term.AbstractTerm;
import pharma.Term.EbiOlsTerm;

import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface EbiOlsRepository extends CrudRepository<AbstractTerm, Long> {

	List<AbstractTerm> findByIri(String iri);
	
	/* NOT synonym, its the label, temporary solution! */
	@Query("SELECT p FROM EbiOlsTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%'))")
	List<AbstractTerm> findBySynonym(String synonym);
	
	/* NOT Final how the parents are identified...*/
	List<AbstractTerm> findByParent(String parent);	
}
