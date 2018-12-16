package pharma.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pharma.OLSCallController;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Term.AbstractTerm;

public abstract class AbstractOlsConnector implements ExternalServiceConnector {

	protected URL url = null;
	
	protected HttpURLConnection conn = null;
	
	protected String iri;
	
	protected static ArrayList<String> visitedTerms = new ArrayList<String>();
	
    private static final Logger logger = LoggerFactory.getLogger(AbstractOlsConnector.class);
	
	/**
	 * Connects to the url set for this class and retrieves all the terms to one IRI as JSONArray
	 * @return
	 * @throws ExternalServiceConnectorException
	 */
	public JSONArray connectAndGetJSON() throws ExternalServiceConnectorException {
		
		try {
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONObject json = null;
		
		try {
			if (conn.getResponseCode() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : "
			        + conn.getResponseCode());
			    }		
	        StringBuilder sb = new StringBuilder();        
	        String line;       
	        BufferedReader br = new BufferedReader(new InputStreamReader(
	                (this.conn.getInputStream())));
	        while ((line = br.readLine()) != null) {
	            sb.append(line);
	        }        
	        
	        if(sb.toString().isEmpty())
	        	throw new ExternalServiceConnectorException("Empty response from " + conn.getURL());	
	        	
	        json = new JSONObject(sb.toString());
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONArray result = new JSONArray();
		
		try {
			// on success: there are multiple children
			result = json.getJSONObject("_embedded").getJSONArray("terms");		
		} catch (JSONException je) {
			// on fail: no children recursion should end.
			result = null;
		}
		
    	return result;
	}
	
	public AbstractTerm retrieveTerm(JSONArray terms, int i, String ontoClass, AbstractTerm pt) {
		JSONObject term = terms.getJSONObject(i);
		
		// Create Entity that will be persisted
		pt.setIri(term.getString("iri"));
				
		String labelString = "\"" + term.getString("label") ;
		
		try {
    		JSONArray synonymsObj = term.getJSONArray("synonyms");
    		
    		for(int j=0; j<synonymsObj.length(); j++) {
    			labelString = labelString + " -- "+synonymsObj.get(j);
    		}
		} catch (JSONException jse) {
			// Do nothing, it's normal...
		}
		
		labelString = labelString + "\"";
		
		pt.setSynonym(labelString);
		pt.setOntoClass(ontoClass);
		
		logger.info("Retrieved: " + pt.getIri());
		
		return pt;
	

	}
		

	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIri(String iri) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getIri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRepo(Object repo) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
	}

}
