package hello;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import hello.EbiOlsTerm;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface EbiOlsRepository extends CrudRepository<EbiOlsTerm, Long> {

	List<EbiOlsTerm> findByIri(String iri);
	
	/* NOT synonym, its the label, temporary solution! */
	@Query("SELECT p FROM EbiOlsTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%'))")
	List<EbiOlsTerm> findBySynonym(String synonym);
	
	/* NOT Final how the parents are identified...*/
	List<EbiOlsTerm> findByParent(String parent);	
}
