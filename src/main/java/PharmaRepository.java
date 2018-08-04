package hello;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import hello.PharmaTerm;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PharmaRepository extends CrudRepository<PharmaTerm, Long> {

	List<PharmaTerm> findByIri(String iri);
}
