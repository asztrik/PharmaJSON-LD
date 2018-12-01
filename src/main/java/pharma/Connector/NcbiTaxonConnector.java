package pharma.Connector;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.NcbiTaxonRepository;
import pharma.Repository.OboNcitRepository;
import pharma.Term.AbstractTerm;

public class NcbiTaxonConnector implements ExternalServiceConnector {
	
	private URL url = null;
	
	private HttpURLConnection conn = null;
	
	private String iri;
	
	private NcbiTaxonRepository ncbiTaxonRepo;
	
	private final String baseUrl = "https://www.ebi.ac.uk/ols/ontologies/ncbitaxon";
	
	public NcbiTaxonConnector() {	}	
	
	@Override
	public HashMap<String, String> queryAndStoreOLS() throws ExternalServiceConnectorException {
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
		this.ncbiTaxonRepo = (NcbiTaxonRepository) repo;
		
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
