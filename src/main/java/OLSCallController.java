package hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class OLSCallController {

	
	@Autowired
	private PharmaRepository pr;
	
	@RequestMapping("/update")
    public String getChildren() {
    	
    	return "ToDo";
    }	
	
    @RequestMapping("/getchildren")
    public String getChildren(@RequestParam(value="iri", defaultValue="GO:0043226") String iri) {
    	
    	return "ToDo";
    }
	
	@RequestMapping("/suggest")
    public String suggest(@RequestParam(value="iri", defaultValue="GO:0043226") String iri) {
        
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
    	}
    
    	
    	
    	
    	// finish JSON
    	// TODO structure it as in the proposal!
    	pharmaJson.put("Suggest", pharmaArray);
    	
    	return pharmaJson.toString();
    	
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

