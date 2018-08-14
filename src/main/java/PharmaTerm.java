package hello;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONArray;
import org.json.JSONObject;

@Entity
public class PharmaTerm {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String iri;
	
	private String synonym;
	
	private String parent;

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
	
	public JSONObject toJSON() {
		JSONObject output = new JSONObject(
				"{PharmaTerm : {"
				+ "\"skos:exactMatch\":\""+this.iri+"\", "
				+ "\"skos:prefLabel\":\""+this.synonym+"\", "
				+ "\"skos:broader\":\""+this.synonym+"\"}"
				+ "}");
		
		return output;
		
	}
	
	
}
