package hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class OLSCallController {

	/**
	 * Make a script that creates the DB and the configs...
	 */
	
	@Autowired
	private PharmaRepository pr;
	
	/**
	 * TEMPORARY TASK FOR THIS METHOD
	 * - nothing
	 * 
	 * WHAT IT SHOULD DO
	 * - go IRI to IRI in the DB and check them against the OLS
	 * - should have an optional IRI parameter
	 * - consider renaming! (update(IRI) and regularUpdate()...)
	 * 
	 * update(IRI):
     * create_new_if_not_exists(IRI)
     * save_labels(IRI)
     * update_parent_path(IRI)
     * children = get_children(IRI)
     * foreach child_IRI in children {
     *   set_child(IRI, child_IRI)
     *   update(child_IRI)
     * }
	 * 
	 * 
	 **/
	@RequestMapping("/update")
    public String update() {
    	
    	return "Work in progres...";
    }	

	/**
	 *  update_parent_path(IRI):
	 *	  parentIRI = get_parent(IRI)
	 *	  create_new_if_not_exists(parentIRI)
	 *	  set_child(parentIRI, IRI)
	 *	  save_labels(parentIRI)
	 *	  update_parent_path(parentIRI)
	 *	}
	 * 
	 * @param iri
	 * @return
	 */
	@RequestMapping("/update_parent_path")
    public String updateParentPath(@RequestParam(value="iri", defaultValue="GO:0043226") String iri) {
    	// Object coming from the OLS directly
    	JSONObject rawJson = callOLS(iri).getContent();
    	
    	// Inner array under "_embedded"
    	JSONArray pharmaArray = new JSONArray();
    	
    	// All the terms for one query as array
    	JSONArray terms = rawJson.getJSONObject("_embedded").getJSONArray("terms");
    	
    	// Resulting JSON, what we will store / pass to the FE
    	JSONObject pharmaJson = new JSONObject();
    	
    	
    	// TODO error handling!  
    	// _embedded field not found, etc...
    	// BLOCKED BY: know the full structure & fields we need...
    
    	String returnstring = "";
    	
    	// get thet term one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
    		
    		
    		JSONObject pharmaTerm = new JSONObject();
    			pharmaTerm.put("skos:exactMatch", term.getString("iri"));
    			pharmaTerm.put("skos:prefLabel", term.getString("label"));
    			// gives error when null...
    			//pharmaTerm.put("rdfs:label", term.getString("synonyms"));
    			pharmaTerm.put("skos:broader", term.getJSONObject("_links").getJSONObject("parents").getString("href"));
    		pharmaArray.put(pharmaTerm);
    		
    		PharmaTerm pt = new PharmaTerm();
    		pt.setIri(term.getString("iri"));
    		pt.setParent(term.getJSONObject("_links").getJSONObject("parents").getString("href"));
    		pt.setSynonym(term.getString("label"));

    		pr.save(pt);
    		
    		returnstring = returnstring + pt.toJSON().toString() + System.lineSeparator();
    		
    	}
    
    	
    	return returnstring;    	
    	
    }		
	
	/**
	 * 
	 * WHAT IT SHOULD DO
	 * - query persisted
	 * - return subtree of the found IRI
	 * 
	 * @param iri
	 * @return
	 */
    @RequestMapping("/getchildren")
    public String getChildren(@RequestParam(value="parent", defaultValue="GO:0043226") String parent) {
 	
    	
    	String retrunstring = "WIP...";
		
		List<PharmaTerm> children = pr.findByParent(parent);
		for (Iterator<PharmaTerm> i = children.iterator(); i.hasNext();) {
			PharmaTerm item = i.next();
			retrunstring = retrunstring + System.lineSeparator() + item.toJSON().toString();
		} 
    	
    	
    	return retrunstring;

    }
	
    /**
     * 
     * WHAT IT DOES:
     * - query persisted data
     * - query by text (i.e. you begin to type "cor" and both CORvus and uniCORn show up...
     * - display results
     * @param iri
     * @return
     */
	@RequestMapping("/suggest")
    public String suggest(@RequestParam(value="label", defaultValue="extra") String label) {      
		
    	String retrunstring = "";
		
		List<PharmaTerm> labels = pr.findBySynonym(label);
		for (Iterator<PharmaTerm> i = labels.iterator(); i.hasNext();) {
			PharmaTerm item = i.next();
			retrunstring = retrunstring + System.lineSeparator() + item.toJSON().toString();
		} 
    	
    	
    	return retrunstring;
    	
    }
    
    
    /**
     * Calls the EBI OLS and gives the result back as a JSON
     * @param iri
     * @return
     */
    private OLSResponse callOLS(String iri) {
    	
    	JSONObject json = null;
        URL url;
		try {
			url = new URL(
			    "https://www.ebi.ac.uk/ols/api/ontologies/go/children?id="+iri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : "
            + conn.getResponseCode());
        }

        StringBuilder sb = new StringBuilder();        
        
        String line;

        
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }        
        
        json = new JSONObject(sb.toString());
        
 
        
		} catch (MalformedURLException p) {
			// TODO Auto-generated catch block
			p.printStackTrace();
		} catch (ProtocolException p) {
			// TODO Auto-generated catch block
			p.printStackTrace();
		} catch (IOException p) {
			// TODO Auto-generated catch block
			p.printStackTrace();
		}
        
    	return new OLSResponse(iri, json);
    }
    
    
    
}

