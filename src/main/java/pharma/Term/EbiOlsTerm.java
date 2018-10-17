package pharma.Term;

import java.util.Collection;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.json.JSONObject;

@Entity
public class EbiOlsTerm extends AbstractTerm {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
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
