package pharma.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;

public interface UniprotRepository extends CrudRepository<AbstractTerm, Long> {
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE p.iri LIKE LOWER(CONCAT('%',:iri, '%')) "
			+ "AND dtype LIKE 'UniprotTerm'")
	List<AbstractTerm> findByIri(String iri);
	
	@Query(value = "SELECT p FROM AbstractTerm p "
			+ "WHERE (p.synonym LIKE LOWER(CONCAT('%',:syn, '%')) "
			+ " OR p.label LIKE LOWER(CONCAT('%',:syn, '%')))"
			+ "AND dtype LIKE 'UniprotTerm'")
	List<AbstractTerm> findBySynonym(@Param("syn") String synonym);
	

	// Find by parent not defined for uniprot
}
