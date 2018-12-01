package pharma.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.EbiOlsRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.EbiOlsTerm;

@Service
public class EbiOlsConnector implements ExternalServiceConnector {

	private URL url = null;
	
	private HttpURLConnection conn = null;
	
	private String iri;
	
	private EbiOlsRepository ebiOlsRepo;
	
	private final String baseUrl = "https://www.ebi.ac.uk/ols/api/ontologies/go/children?id=";
	
	public EbiOlsConnector() {	}	
	
	public EbiOlsConnector(URL url, HttpURLConnection conn, String iri, EbiOlsRepository eor) {
		this.iri = iri;
		this.url = url;
		this.conn = conn;
		this.ebiOlsRepo = eor;
	}
	
	public EbiOlsConnector(String iri, EbiOlsRepository eor) {
		
		this.ebiOlsRepo = eor;
		this.iri = iri;
		
		try {
			this.url = new URL(
					baseUrl+this.iri);
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
		
		try {
			this.url = new URL(
					baseUrl+this.iri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Connects to the url set for this class and retrieves all the terms to one IRI as JSONArray
	 * @return
	 * @throws ExternalServiceConnectorException
	 */
	private JSONArray connectAndGetJSON() throws ExternalServiceConnectorException {
		
		System.out.println("URL called " + url);
		
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
		
		
    	return json.getJSONObject("_embedded").getJSONArray("terms");		
	}
	
	
	/**
	 * Gets only the parents of a term as a list
	 * @param url
	 * @return
	 * @throws ExternalServiceConnectorException
	 */
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException {
		
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		JSONArray terms = connectAndGetJSON();
    	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
    		
    		// At this moment there are no parents to be found, need to do the linking later!!!
    		List<AbstractTerm> child = this.ebiOlsRepo.findByIri(childIri);
    		if(!child.isEmpty()) {
    			System.out.println("Child: " + child.get(0).getIri() + " - " + child.get(0).toString());
    			System.out.println("Term: " + term.getString("iri"));
    			List<AbstractTerm> parent = this.ebiOlsRepo.findByIri(term.getString("iri"));
    			if(!parent.isEmpty()) {
    				System.out.println("Parent: " + parent.get(0).getIri());
    				child.get(0).setParent(parent.get(0));
    				ebiOlsRepo.save(child.get(0));
    			}
    		}
    	}
	
	}

	/**
	 * Connects to the URL set and gets the terms as JSON, also sets the parents
	 * Returns a dictionary with the links to the parents of the terms.
	 * How the Dict looks like:
	 * [TermIri] --> [ParentLink]
	 * Both unique.
	 */
	@Override
	public HashMap<String, String> queryAndStoreOLS() throws ExternalServiceConnectorException {
		
		if(this.iri.isEmpty() || this.iri == null) {
				throw new ExternalServiceConnectorException("Iri is not set, the OLS would give 404");	
		}
	
		// All the terms for one query as array
    	JSONArray terms = connectAndGetJSON();
    	
    	HashMap<String, String> parentLinkList = new HashMap<String, String>();
    	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
    		
    		// Create Entity that will be persisted
    		EbiOlsTerm pt = new EbiOlsTerm();
    		pt.setIri(term.getString("iri"));
    		
    		parentLinkList.put( term.getString("iri"), term.getJSONObject("_links").getJSONObject("parents").getString("href"));
    		
    		System.out.println(term.getString("iri") + " saved.");
    		
    		pt.setSynonym(term.getString("label"));

    		try {
	    		JSONArray synonymsObj = term.getJSONArray("synonyms");
	    		
	    		System.out.println("+++ SYNONYMS FOUND: +++");
	    		for(int j=0; j<synonymsObj.length(); j++) {
	    			System.out.println(synonymsObj.get(j));
	    		}
    		} catch (JSONException jse) {
    			// Do nothing, it's normal...
    		}
    		

		
    		ebiOlsRepo.save(pt);
  
    		
    	}
    	
    	return parentLinkList;
    
	}
	
	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRepo(Object repo) {
		this.ebiOlsRepo = (EbiOlsRepository)repo;
		
	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
	}

}
