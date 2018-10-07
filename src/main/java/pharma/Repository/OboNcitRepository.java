package pharma.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pharma.Term.OboNcitTerm;

public interface OboNcitRepository extends CrudRepository<OboNcitTerm, Long> {

		List<OboNcitTerm> findByIri(String iri);
		
		/* NOT synonym, its the label, temporary solution! */
		@Query("SELECT p FROM EbiOlsTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%'))")
		List<OboNcitTerm> findBySynonym(String synonym);
		
		/* NOT Final how the parents are identified...*/
		List<OboNcitTerm> findByParent(String parent);	
}
