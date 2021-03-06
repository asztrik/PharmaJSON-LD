package pharma.Connector;

import java.util.HashMap;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Term.AbstractTerm;

public interface ExternalServiceConnector {
	
	// Calls a given OLS and parses term(s)
	public HashMap<String, String> queryAndStoreOLS(String ontoClass) throws ExternalServiceConnectorException;
	
	// Retrieves a previously persisted term as a JSON Object
	public AbstractTerm retrieveAsJSON(String iri);
	
	// Links the parents to the children
	public void linkParents(String url, String childIri) throws ExternalServiceConnectorException;
	
	public void setIri(String iri);
	
	public String getIri();
	
	public void setRepo(Object repo);
	
	public Object getRepo();
	
	public void saveOne(String iri, String ontoclass);
	
	public AbstractTerm saveOneTerm(String ontoClass, AbstractTerm pt) throws ExternalServiceConnectorException;

}
