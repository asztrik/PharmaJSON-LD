package pharma.Connector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    
    protected static ArrayList<String> visitedTerms = new ArrayList<String>();
    
    private String iri;
    
    private URL url;
    
    protected static HashMap<String, String> parentLinkList = new HashMap<String, String>();
	
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
		
		
		try {
			doc = Jsoup.connect(url.toString()).get();
		} catch (IOException e) {
			e.printStackTrace();
			return parentLinkList;
		} catch (Exception e) {
			e.printStackTrace();
			return parentLinkList;
		}
		
		
		String title = doc.title();

		CellosaurusTerm term = new CellosaurusTerm();
		
		term.setIri(url.toString());
		term.setLabel(title);
		term.setOntoClass(ontoClass);
		
		
		try {
			CellosaurusRepo.save(term);
		} catch (DataIntegrityViolationException e) {
			logger.info(term.getIri() + " - duplicate IRI, not saved.");
		}		
		
	    Elements inputElements = doc.getElementsByTag("th");  
	    for (Element inputElement : inputElements) {   
	    	if(inputElement.text().equals("Hierarchy")) {	
	    		if(inputElement.nextElementSibling().text().substring(0, 8).equals("Children")) {
	    		// Cell begins with "Children": save all the href-targets as list of children
	    			for (Element childLink : inputElement.nextElementSibling().getElementsByTag("a")) {
	    				parentLinkList.putAll(processChildTerm(childLink.text(), parentLinkList, ontoClass));
	    			}    	            
	    		} else if(inputElement.nextElementSibling().text().substring(0, 6).equals("Parent")) {
	    		  // There is always only 1 parent in Cellosaurus
	    			Element parentLink = inputElement.nextElementSibling().getElementsByTag("a").get(0);
	    			parentLinkList.put(iri, parentLink.text());	    				
	    			int index = 0;	
	    		    // After that there might be some children too:
					for (Element childLink : inputElement.nextElementSibling().getElementsByTag("a")) {
						// Omit the first element, since it must be a parent we just processed
						if(index > 0) {
							parentLinkList.putAll(processChildTerm(childLink.text(), parentLinkList, ontoClass));
						}
						index++;
					}
	    		}
	    		
	    	}
	    } 
			
	    
		return parentLinkList;
	}

	
	private HashMap<String, String> processChildTerm(String childLinkText, HashMap<String, String> parentLinkList, String ontoClass) throws ExternalServiceConnectorException {

		if(!visitedTerms.contains(childLinkText)) {
			visitedTerms.add(childLinkText);
			// go recursive

			this.setIri(childLinkText);
			
			// add iri to the parent linking map
			parentLinkList.putAll(this.queryAndStoreOLS(ontoClass));
			
		}
		
		return parentLinkList;
	}
	
	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkParents(String parentIri, String childIri) throws ExternalServiceConnectorException {

		List<AbstractTerm> parent = this.CellosaurusRepo.findByIri(parentIri);
		if(!parent.isEmpty()) {
			List<AbstractTerm> child = this.CellosaurusRepo.findByIri(childIri);
			if(!child.isEmpty()) {
				child.get(0).setParent(parent);
				CellosaurusRepo.save(child.get(0));
			}
		}
		
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

	@Override
	public void saveOne(String iri, String ontoclass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractTerm saveOneTerm(String ontoClass, AbstractTerm pt) throws ExternalServiceConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

}
