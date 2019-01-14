package pharma.Connector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.CellosaurusRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.CellosaurusTerm;

public class CellosaurusConnector implements ExternalServiceConnector {

	
	protected CellosaurusRepository CellosaurusRepo;
	
	private String UrlBase = "https://web.expasy.org/cellosaurus/";
	
    private static final Logger logger = LoggerFactory.getLogger(CellosaurusConnector.class);
    
    private String iri;
    
    private URL url;
	
	public CellosaurusConnector() {	}
	
	public CellosaurusConnector(String iri, CellosaurusRepository eor) {
		this.CellosaurusRepo = eor;
		this.iri = iri;
		
		try {
			this.url = new URL(
					UrlBase+this.iri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException {

		Document doc = null;
		HashMap<String, String> parentLinkList = new HashMap<String, String>();
		
		try {
			doc = Jsoup.connect(url.toString()).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String title = doc.title();

	    Elements inputElements = doc.getElementsByTag("th");  
	    for (Element inputElement : inputElements) {   
	    	if(inputElement.text().equals("Hierarchy")) {
	    		// Gets "Parent: + IRI
		        System.out.println(" "+inputElement.nextElementSibling().text());
	    	}
	    } 
		
		CellosaurusTerm term = new CellosaurusTerm();
		
		term.setIri(iri);
		term.setLabel(title);
		
		//parentLinkList.put(UrlBase+parent, iri);
	
		try {
			CellosaurusRepo.save(term);
		} catch (DataIntegrityViolationException e) {
			logger.info(term.getIri() + " - duplicate IRI, not saved.");
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

		return;
	}

	@Override
	public void setIri(String iri) {
		this.iri = iri;
		try {
			this.url = new URL(
					UrlBase+this.iri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public String getIri() {
		return iri;
	}

	@Override
	public void setRepo(Object repo) {
		this.CellosaurusRepo = (CellosaurusRepository)repo;

	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
	}

}
