package pharma.Term;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "PARENT_OF")
public class Parent {

    @Id @GeneratedValue private Long id;
    private List<String> children = new ArrayList<>();

    @StartNode
    private AbstractTerm person;

    @EndNode
    private AbstractTerm movie;
}