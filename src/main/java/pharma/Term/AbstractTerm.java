package pharma.Term;


import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


import java.util.List;

import org.json.JSONObject;

//@MappedSuperclass
@NodeEntity
public abstract class AbstractTerm {
	
	// An Id is needed for the Persistence API
	@Id @GeneratedValue private Long id;

	// Here are the fields that all the Terms have
	protected String iri;
	
	protected String label;

	protected String synonym;
	
	protected String ontoclass;
	
	/*** THIS SHOULD BE MANY-TO-MANY... ***/
	
	@Relationship(type = "CHILD_OF", direction = Relationship.OUTGOING)
	protected List<Parent> hierarchy;

	
	// All the Terms need a method that converts them to a JSON
	// But the exact format / content depends on the FE fields...
    /// you may redefine them at the individual Term Classes
	public JSONObject toJSON() {
		
		// create base JSON-LD
		JSONObject output = new JSONObject();
		
		// add type, fixed
		output.append("@type", "skos:Concept");
		// add an ID, which is the IRI of the term
		output.append("@ID", this.iri);
		
		// sub-object for storing the label + language info
		JSONObject labelObject = new JSONObject();		
		labelObject.put("@value", this.label);
		labelObject.put("@language", "@eng");
		
		output.append("skos:prefLabel", labelObject);
		
		// sub-object for storing the label + language info		
		JSONObject synonymObject = new JSONObject();		
		synonymObject.put("@value", this.synonym);
		synonymObject.put("@language", "@eng");
		
		output.append("skos:altLabel", synonymObject);
		
		return output;
	}
	
	// getters and setters...
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public void setParent(List<Parent> parentlist) {
		this.hierarchy = parentlist;
	}

	
	public String getOntoClass() {
		return ontoclass;
	}

	public void setOntoClass(String ontoClass) {
		this.ontoclass = ontoClass;
	}
	
	
}


