package pharma.Term;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import org.json.JSONObject;

//@MappedSuperclass
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class AbstractTerm {
	
	// An Id is needed for the Persistence API
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

	// Here are the fields that all the Terms have
	@Column(unique=true)
	protected String iri;
	
	protected String label;

	@Column(columnDefinition="TEXT")
	protected String synonym;
	
	protected String ontoclass;
	
	/*** THIS SHOULD BE MANY-TO-MANY... ***/
	
    @ManyToOne(targetEntity = AbstractTerm.class, optional=true)
    @JoinColumn(name="PARENT_ID", nullable=true)
    protected AbstractTerm parent;
	
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
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public void setParent(AbstractTerm parentlist) {
		this.parent = parentlist;
	}

	
	public String getOntoClass() {
		return ontoclass;
	}

	public void setOntoClass(String ontoClass) {
		this.ontoclass = ontoClass;
	}
	
	
	
}
