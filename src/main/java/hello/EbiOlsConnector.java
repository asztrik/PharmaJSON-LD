package hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class EbiOlsConnector implements ExternalServiceConnector {

	private URL url = null;
	
	private HttpURLConnection conn = null;
	
	private String iri;
	
	@Autowired
	private EbiOlsRepository pr;
	
	EbiOlsConnector(URL url, HttpURLConnection conn, String iri) {
		this.iri = iri;
		this.url = url;
		this.conn = conn;
	}
	
	EbiOlsConnector(String iri) {
		
		this.iri = iri;
		
		try {
			this.url = new URL(
				    "https://www.ebi.ac.uk/ols/api/ontologies/go/children?id="+this.iri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		
		try {
			this.conn = (HttpURLConnection) this.url.openConnection();
			this.conn.setRequestMethod("GET");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.conn.setRequestProperty("Accept", "application/json");
	}
	
	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	@Override
	public void queryAndStoreOLS() throws ExternalServiceConnectorException {
		
		if(this.iri.isEmpty() || this.iri == null) {
				throw new ExternalServiceConnectorException("Iri is not set, the OLS would give 404");	
		}

		// raw JSON
		JSONObject json = null;
		
    	// Inner array under "_embedded"
    	//JSONArray pharmaArray = new JSONArray();
		
		try {
			if (this.conn.getResponseCode() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : "
			        + this.conn.getResponseCode());
			    }


	        StringBuilder sb = new StringBuilder();        
	        
	        String line;

	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(
	                (this.conn.getInputStream())));
	        while ((line = br.readLine()) != null) {
	            sb.append(line);
	        }        
	        
	         json = new JSONObject(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO terms might be null
		
		// All the terms for one query as array
    	JSONArray terms = json.getJSONObject("_embedded").getJSONArray("terms");
    	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
    		
    		// Create a JSON directly (testing)
    		//JSONObject pharmaTerm = new JSONObject();
    		//	pharmaTerm.put("skos:exactMatch", term.getString("iri"));
    		//	pharmaTerm.put("skos:prefLabel", term.getString("label"));
    		//	// gives error when null...
    		//	//pharmaTerm.put("rdfs:label", term.getString("synonyms"));
    		//	pharmaTerm.put("skos:broader", term.getJSONObject("_links").getJSONObject("parents").getString("href"));
    		//pharmaArray.put(pharmaTerm);
    		
    		// Create Entity that will be persisted
    		EbiOlsTerm pt = new EbiOlsTerm();
    		pt.setIri(term.getString("iri"));
    		pt.setParent(term.getJSONObject("_links").getJSONObject("parents").getString("href"));
    		pt.setSynonym(term.getString("label"));

    		pr.save(pt);
  
    		
    	}
    
	}
	
	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
	}

}
