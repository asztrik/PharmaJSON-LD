package pharma.Connector;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.ChebiRepository;
import pharma.Term.AbstractTerm;

public class ChebiConnector implements ExternalServiceConnector {

	
	private URL url = null;
	
	private HttpURLConnection conn = null;
	
	private String iri;
	
	private ChebiRepository chebiRepo;
	
	private final String baseUrl = "https://www.ebi.ac.uk/chebi/";
	
	public ChebiConnector() {	}	
	
	@Override
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTerm retrieveAsJSON(String iri) {
		// TODO Auto-generated method stub
		return null;
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
		this.chebiRepo = (ChebiRepository) repo;
		
	}

	@Override
	public Object getRepo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException {
		// TODO Auto-generated method stub
		
	}

}
