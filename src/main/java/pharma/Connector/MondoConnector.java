package pharma.Connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.MondoRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.MondoTerm;

public class MondoConnector extends AbstractOlsConnector {

	protected MondoRepository MondoRepo;
	
	protected final String baseUrl = "https://www.ebi.ac.uk/ols/api/ontologies/mondo/children?id=";
	
	public MondoConnector() {	}	
	
	public MondoConnector(URL url, HttpURLConnection conn, String iri, MondoRepository eor) {
		this.iri = iri;
		this.url = url;
		this.conn = conn;
		this.MondoRepo = eor;
	}
	
	public MondoConnector(String iri, MondoRepository eor) {
		
		this.MondoRepo = eor;
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
    		List<AbstractTerm> child = this.MondoRepo.findByIri(childIri);
    		if(!child.isEmpty()) {
    			List<AbstractTerm> parent = this.MondoRepo.findByIri(term.getString("iri"));
    			if(!parent.isEmpty()) {
    				child.get(0).setParent(parent.get(0));
    				MondoRepo.save(child.get(0));
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
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException {
		
		if(this.iri.isEmpty() || this.iri == null) {
				throw new ExternalServiceConnectorException("Iri is not set, the OLS would give 404");	
		}
	
		// All the terms for one query as array
    	JSONArray terms = connectAndGetJSON();
    	
    	HashMap<String, String> parentLinkList = new HashMap<String, String>();
    	
		if(terms == null)
			return parentLinkList; // return empty list if there are no more children.
   	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {

    		MondoTerm term = new MondoTerm();
    		
    		term = (MondoTerm)retrieveTerm(terms, i,  ontoClass, term);
    		
    		parentLinkList.put( term.getIri(), terms.getJSONObject(i).getJSONObject("_links").getJSONObject("parents").getString("href"));
    		
    		
    		MondoRepo.save(term);
    		
    		
    		// Now get the iri
    		String iri = term.getIri();
    		
    		// Format for the next request
    		iri.replace("http://purl.obolibrary.org/obo/", "");
    		iri.replace("_", ":");
    		
    		if(!visitedTerms.contains(iri)) {
    			visitedTerms.add(iri);
    			// go recursive
    			this.setIri(iri);
    			parentLinkList.putAll(this.queryAndStoreOLS(ontoClass));
    		}		
    		
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
		this.MondoRepo = (MondoRepository)repo;
		
	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
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
		

}