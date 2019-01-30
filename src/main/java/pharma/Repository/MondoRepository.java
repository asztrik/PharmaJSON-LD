package pharma.Repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pharma.Term.AbstractTerm;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface MondoRepository extends AbstractRepository {
	
}
