package pharma.Connector;

import java.util.HashMap;
import java.util.List;

import pharma.Exception.ExternalServiceConnectorException;
import pharma.Term.AbstractTerm;

public interface ExternalServiceConnector {
	
	// Calls a given OLS and parses term(s)
	public HashMap<String, String> queryAndStoreOLS() throws ExternalServiceConnectorException;
	
	// Retrieves a previously persisted term as a JSON Object
	public AbstractTerm retrieveAsJSON(String iri);
	
}
