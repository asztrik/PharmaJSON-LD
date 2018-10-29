package pharma.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pharma.Term.AbstractTerm;

public interface OboNcitRepository extends CrudRepository<AbstractTerm, Long> {
	
		@Query("SELECT p FROM AbstractTerm p WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) AND dtype LIKE 'OboNcitTerm'")
		List<AbstractTerm> findByIri(String iri);
		
		@Query("SELECT p FROM AbstractTerm p WHERE p.synonym LIKE LOWER(CONCAT('%',:synonym, '%')) AND dtype LIKE 'OboNcitTerm'")
		List<AbstractTerm> findBySynonym(String synonym);
		
		@Query("SELECT p FROM AbstractTerm p WHERE p.parent = parent AND dtype LIKE 'OboNcitTerm'")
		List<AbstractTerm> findByParent(Integer parent);	
}
