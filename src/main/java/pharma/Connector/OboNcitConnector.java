package pharma.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import pharma.Exception.ExternalServiceConnectorException;

import pharma.Repository.OboNcitRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.EbiOlsTerm;
import pharma.Term.OboNcitTerm;

public class OboNcitConnector implements ExternalServiceConnector {

	private URL url = null;
	
	private HttpURLConnection conn = null;
	
	private String iri;
	
	private OboNcitRepository pr;
	
	OboNcitConnector() {	}	
	
	OboNcitConnector(URL url, HttpURLConnection conn, String iri, OboNcitRepository eor) {
		this.iri = iri;
		this.url = url;
		this.conn = conn;
		this.pr = eor;
	}
	
	public OboNcitConnector(String iri, OboNcitRepository eor) {
		
		this.pr = eor;
		this.iri = iri;
		
		try {
			this.url = new URL(
				    "https://www.ebi.ac.uk/ols/api/ontologies/ncit/terms?id="+this.iri);
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
		
		// raw JSON
		JSONObject json = null;
		
		try {
			if (this.conn.getResponseCode() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : "
			        + this.conn.getResponseCode() + " with URL " + this.conn.getURL());
			    }


	        StringBuilder sb = new StringBuilder();        
	        
	        String line;

	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(
	                (this.conn.getInputStream())));
	        while ((line = br.readLine()) != null) {
	        	System.out.println(line);
	            sb.append(line);
	        }        
	        
	       
	        if(sb.toString().isEmpty())
	        	throw new ExternalServiceConnectorException("Empty response from OboNcit while querying " + this.conn.getURL());	
	        	
	        json = new JSONObject(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// All the terms for one query as array
    	JSONArray terms = json.getJSONObject("_embedded").getJSONArray("terms");
    	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
   		
    		// Create Entity that will be persisted
    		OboNcitTerm pt = new OboNcitTerm();
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
