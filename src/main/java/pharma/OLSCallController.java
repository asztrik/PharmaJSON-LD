package pharma;


import java.util.List;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pharma.Connector.EbiOlsConnector;
import pharma.Connector.OboNcitConnector;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.EbiOlsRepository;
import pharma.Repository.OboNcitRepository;
import pharma.Term.AbstractTerm;
import pharma.Term.EbiOlsTerm;
import pharma.Term.OboNcitTerm;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class OLSCallController {

	/**
	 * 
	 * MINOR TODOS
	 * 
	 * Make a script that creates the DB and the configs...
	 * Why does the db go away on server reboot??
	 * Duplicate entries?
	 * Display the JSON in browser right
	 *  
	 */
	
	@Autowired
	private EbiOlsRepository pr;
	
	@Autowired
	private OboNcitRepository nr;
	
	private EbiOlsConnector eoc;
	private OboNcitConnector ncc;
	
	/**
	 * TEMPORARY TASK FOR THIS METHOD
	 * - nothing
	 * 
	 * WHAT IT SHOULD DO
	 * - go IRI to IRI in the DB and check them against the OLS
	 * - should have an optional IRI parameter
	 * - consider renaming! (update(IRI) and regularUpdate()...)
	 * 
	 * update(IRI):
     * create_new_if_not_exists(IRI)
     * save_labels(IRI)
     * update_parent_path(IRI)
     * children = get_children(IRI)
     * foreach child_IRI in children {
     *   set_child(IRI, child_IRI)
     *   update(child_IRI)
     * }
	 * 
	 * 
	 **/
	@RequestMapping("/update")
    public String update() {
    	
		// Query and store all EBI OLS terms...
		
		
		/* [TermIri] --> [ParentLink] */
		HashMap<String, String> urlsOfParents = new HashMap<String, String>();
		
		try {
			
			// #ERROR the 2 parents themselves are NEVER listed as terms and NOT persisted! 
			
			/* now always concatenate the baselist with the returns of query&store */
			eoc = new EbiOlsConnector("GO:0005575", pr); // parent of 0043226
			urlsOfParents.putAll(eoc.queryAndStoreOLS());
			eoc = new EbiOlsConnector("GO:0043226", pr);
			urlsOfParents.putAll(eoc.queryAndStoreOLS());
			//eoc = new EbiOlsConnector("GO:0043231", pr);
			//urlsOfParents.putAll(eoc.queryAndStoreOLS());
			//...
			
			
			for(Entry<String, String> entry : urlsOfParents.entrySet()) {
				System.out.println("getParentByURL: "+entry.getValue()+" - "+entry.getKey());
				eoc.getParentByURL(entry.getValue(), entry.getKey());
			}

		} catch (ExternalServiceConnectorException e) {

			e.printStackTrace();
		}
		
		// Query and store all OboNcit terms...
//		try {		
//			ncc = new OboNcitConnector("GO:0044834", nr);
//			ncc.queryAndStoreOLS();
//			ncc = new OboNcitConnector("GO:0044834", nr);
//			ncc.queryAndStoreOLS();
//		} catch (ExternalServiceConnectorException e) {
//	
//			e.printStackTrace();
//		}		
		// Report Success.
		return "{ \"updateStatus\": \"success\"}";
    }	

	/**
	 *  update_parent_path(IRI):
	 *	  parentIRI = get_parent(IRI)
	 *	  create_new_if_not_exists(parentIRI)
	 *	  set_child(parentIRI, IRI)
	 *	  save_labels(parentIRI)
	 *	  update_parent_path(parentIRI)
	 *	}
	 * 
	 * @param iri
	 * @return
	 */
	@RequestMapping("/update_parent_path")
    public String updateParentPath(@RequestParam(value="iri", defaultValue="GO:0043226") String iri) {
    	
    	return "WIP...";    	
    	
    }		
	
	/**
	 * 
	 * WHAT IT SHOULD DO
	 * - query persisted
	 * - return subtree of the found IRI
	 * 
	 * @param iri
	 * @return
	 */
    @RequestMapping("/getchildren")
    public String getChildren(@RequestParam(value="parent", defaultValue="GO:0043226") String parent) {
 	
    	
    	String retrunstring = "WIP...";
		
		List<AbstractTerm> children = pr.findByParent(parent);
		for (Iterator<AbstractTerm> i = children.iterator(); i.hasNext();) {
			AbstractTerm item = i.next();
			retrunstring = retrunstring + System.lineSeparator() + item.toJSON().toString();
		} 
    	
    	
    	return retrunstring;

    }
	
    /**
     * 
     * WHAT IT DOES:
     * - query persisted data
     * - query by text (i.e. you begin to type "cor" and both CORvus and uniCORn show up...
     * - display results
     * @param iri
     * @return
     */
	@RequestMapping("/suggest")
    public String suggest(
    		@RequestParam(value="label", defaultValue="extra") String label,
    		@RequestParam(value="ontology", defaultValue="go") String ontology) {      
		
		//Add JSON wrapper (same for one OLS) -- how to handle different OLS-es here??
    	String returnstring = "{\"@context\": { \"GOCellComp\": \"http://purl.obolibrary.org/obo/GO_0005575\" }," + 
    			"\""+label+"\": [ ";
	
    	
    	switch(ontology) {
    	case "go":
    		List<AbstractTerm> labelsGo;
    		labelsGo = pr.findBySynonym(label);
    		for (Iterator<AbstractTerm> i = labelsGo.iterator(); i.hasNext();) {
    			AbstractTerm item = i.next();
    			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
    			returnstring = returnstring + ",";
    		}     		
    		break;
    	case "ncit": 
    		List<OboNcitTerm> labelsNcit;
    		labelsNcit = nr.findBySynonym(label);
    		for (Iterator<OboNcitTerm> i = labelsNcit.iterator(); i.hasNext();) {
    			OboNcitTerm item = i.next();
    			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
    			returnstring = returnstring + ",";
    		}     		
    		break;
    	}
		
    	
		// remove last comma
		returnstring = returnstring.substring(0, returnstring.length()-1);
		
    	//Add end of JSON wrapper
		returnstring = returnstring + " ] }";
		
    	return returnstring;
    	
    }

}

