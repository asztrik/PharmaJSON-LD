package pharma.Term;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.json.JSONObject;

@MappedSuperclass
public abstract class AbstractTerm {
	
	// An Id is needed for the Persistence API
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	// Here are the fields that all the Terms have
	protected String iri;
	
	protected String label;

	protected String synonym;
	
	protected String parent;

	// All the Terms need a method that converts them to a JSON
	// But the exact format / content depends on the FE fields...
	public abstract JSONObject toJSON();
	
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	

	
}
