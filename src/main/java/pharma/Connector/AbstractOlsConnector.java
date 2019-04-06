package pharma.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public JSONArray connectAndGetJSON(ConnectionPurpose cp) throws ExternalServiceConnectorException {
		
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
			
			if(cp.equals(ConnectionPurpose.TERMS)) {
				// on success: there are multiple children
				result = json.getJSONObject("_embedded").getJSONArray("terms");
			} else if(cp.equals(ConnectionPurpose.PAGES)) {
				result = new JSONArray();
				result.put(json.getJSONObject("page").get("totalElements"));
			}
			
					
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
				
		String labelString = "\"" + term.getString("label");
		
		pt.setLabel(term.getString("label"));
		
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
		

	/**
	 * Helper function to fix the paging problem of the OLS
	 * The OLS returns by default only a 20 long page of the child-list
	 * This excludes some elements of the result. Appending the ?size=N& to
	 * the requests makes all the terms available on one page.
	 * 
	 * Why so and why not handle paging?
	 * 
	 * A JSON of 20 records is 16 kB, approx. 2000 children would result 
	 * in a ~ 2 MB response, which is still OK to handle. Note: this is
	 * only a part of the update method, which is only supposed to run 
	 * weekly to fill the DB and not to serve user requests.
	 * @param originalUrl
	 * @return
	 */
	public String appendPageToBaseurl(String originalUrl) {
		
		int totalElementNum = 0;
		
		// first load the page
		try {
			this.url = new URL(originalUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		
		//extract the "pages" Object & get total size
		try {
			totalElementNum = (int) connectAndGetJSON(ConnectionPurpose.PAGES).get(0);
		} catch (ExternalServiceConnectorException e) {
			e.printStackTrace();
		}		
		
		// append size parameter
		String newUrl = originalUrl.replace("?id", "?size=" + String.valueOf(totalElementNum) + "&id");
		return newUrl;
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
