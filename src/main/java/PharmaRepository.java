package hello;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import hello.PharmaTerm;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PharmaRepository extends CrudRepository<PharmaTerm, Long> {

	List<PharmaTerm> findByIri(String iri);
	
	/* NOT synonym, its the label, temporary solution! */
	@Query("SELECT p FROM PharmaTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%'))")
	List<PharmaTerm> findBySynonym(String synonym);
	
	/* NOT Final how the parents are identified...*/
	List<PharmaTerm> findByParent(String parent);	
}
