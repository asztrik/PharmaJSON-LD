package pharma.Connector;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Term.AbstractTerm;

public interface ExternalServiceConnector {
	
	// Calls a given OLS and parses term(s)
	public void queryAndStoreOLS() throws ExternalServiceConnectorException;
	
	// Retrieves a previously persisted term as a JSON Object
	public AbstractTerm retrieveAsJSON(String iri);
	
}
