package pharma.Connector;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.xml.sax.InputSource;


import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.UniprotRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.UniprotTerm;

public class UniprotConnector implements ExternalServiceConnector {

	
	protected UniprotRepository UniProtRepo;
	
	private String xmlUrlBase = "https://www.uniprot.org/uniprot/?query=reviewed:yes&format=xml&limit=10";
	
    private static final Logger logger = LoggerFactory.getLogger(UniprotConnector.class);
	
	public UniprotConnector() {	}
	
	@Override
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException {

	    try {

	    	SAXParserFactory spf = SAXParserFactory.newInstance();
	    	SAXParser sp = spf.newSAXParser();
	    	URL linkURL = new URL(xmlUrlBase);
	    	UniprotHandler uniprotHandler = new UniprotHandler();
	    	
	    	// Parse the whole XML into a List of terms
	    	sp.parse(new InputSource(linkURL.openStream()), uniprotHandler);

	    	
	    	// traverse the term list and save them. This is separated from the parsing
	    	// for the case that some other method was needed here (like linkParents)
	    	for (Iterator<UniprotTerm> i = uniprotHandler.getTerms().iterator(); i.hasNext();) {
	    	    UniprotTerm term = i.next();
	    	    term.setOntoClass(ontoClass);
	    	    /// if there is no synonym, set the field to the label so we do not get an invalid JSON
	    	    if(term.getSynonym() == null || term.getSynonym().isEmpty())
	    	    	term.setSynonym(term.getLabel());
	    		try {
		    		this.UniProtRepo.save(term);
	    		} catch (DataIntegrityViolationException e) {
	    			logger.info(term.getIri() + " - duplicate IRI, not saved.");
	    		}
	    	}
		
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
	    // need to return this type
		return new HashMap<String, String>();
	}

	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException {
		// No parent linking for UniProt necessary

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
		this.UniProtRepo = (UniprotRepository)repo;
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
