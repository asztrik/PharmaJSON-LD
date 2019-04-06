package pharma.Connector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.BaoRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.BaoTerm;

public class BaoConnector  extends AbstractOlsConnector {

	protected BaoRepository baoRepo;
	
	protected String baseUrl = "https://www.ebi.ac.uk/ols/api/ontologies/bao/terms/";
	
	
	
    private static final Logger logger = LoggerFactory.getLogger(BaoConnector.class);
	
	public BaoConnector() {	}	
	
	public BaoConnector(URL url, HttpURLConnection conn, String iri, BaoRepository repo) {
		this.iri = iri;
		this.url = url;
		this.conn = conn;
		this.baoRepo = repo;
	}    
    
	public BaoConnector(String iri, BaoRepository repo) {
		
		this.baoRepo = repo;
		this.iri = iri;

		// This adds a &size parameter to the URL so that it returns
		// all the terms, without paging
		this.baseUrl = appendPageToBaseurl(this.baseUrl);

		
		try {
			this.url = new URL(
					baseUrl+this.iri+"/children");
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
	
	@Override
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException {
		if(this.iri.isEmpty() || this.iri == null) {
			throw new ExternalServiceConnectorException("Iri is not set, the OLS would give 404");	
	}

	// All the terms for one query as array
	JSONArray terms = connectAndGetJSON(ConnectionPurpose.TERMS);
	
	HashMap<String, String> parentLinkList = new HashMap<String, String>();
	
	if(terms == null)
		return parentLinkList; // return empty list if there are no more children.
	
	// get the terms one by one
	for (int i=0; i < terms.length(); i++) {

		BaoTerm term = new BaoTerm();
		
		term = (BaoTerm)retrieveTerm(terms, i,  ontoClass, term);
		
		parentLinkList.put( term.getIri(), terms.getJSONObject(i).getJSONObject("_links").getJSONObject("parents").getString("href"));
		
		try {
			baoRepo.save(term);
		} catch (DataIntegrityViolationException e) {
			logger.info(term.getIri() + " - duplicate IRI, not saved.");
		}    		
		
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
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		JSONArray terms = connectAndGetJSON(ConnectionPurpose.TERMS);
		  	
    	// get the terms one by one
    	for (int i=0; i < terms.length(); i++) {
    		JSONObject term = terms.getJSONObject(i);
    		
    		// At this moment there are no parents to be found, need to do the linking later!!!
    		List<AbstractTerm> child = this.baoRepo.findByIri(childIri);
    		if(!child.isEmpty()) {
    			List<AbstractTerm> parent = this.baoRepo.findByIri(term.getString("iri"));
    			if(!parent.isEmpty()) {
    				child.get(0).setParent(parent);
    				baoRepo.save(child.get(0));
    			}
    		}
    	}

	}

	@Override
	public void setIri(String iri) {
		this.iri = iri;
		try {
			this.url = new URL(
					baseUrl+URLEncoder.encode(URLEncoder.encode(this.iri, "UTF-8"), "UTF-8")+"/children");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("URL set to: " + this.url);
	}

	@Override
	public String getIri() {
		return iri;
	}

	@Override
	public void setRepo(Object repo) {
		this.baoRepo = (BaoRepository)repo;

	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
	}

}
