package pharma.Term;

import javax.persistence.Entity;


import org.json.JSONObject;

@Entity
public class NcbiTaxonTerm extends AbstractTerm {

	
	public JSONObject toJSON () {
		
		JSONObject output = new JSONObject(
				"{ \"@type\":\"skos:Concept\","
				+ "\"skos:prefLabel\": [ { \"@value\": \""+this.synonym+"\", "
				+ "\"@language\":\"eng\"} ], \"rdfs:label\":[ { \"@value\": " 
				+ "\"" +this.synonym+"\", \"@language\":\"eng\" } ],"
				+ "\"skos:exactMatch\":\""+this.iri+"\"} "
				);
		
		return output;
		
	}


	
	
}
