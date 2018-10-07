package pharma;


import java.util.List;
import java.util.Iterator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pharma.Connector.EbiOlsConnector;
import pharma.Connector.OboNcitConnector;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.EbiOlsRepository;
import pharma.Repository.OboNcitRepository;
import pharma.Term.EbiOlsTerm;

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
		eoc = new EbiOlsConnector("GO:0043226", pr);
		ncc = new OboNcitConnector("NCIT_C19160", nr);
		
		try {
			eoc.queryAndStoreOLS();
			eoc.setIri("GO:0043231");
			eoc.queryAndStoreOLS();
			
			//...
			ncc.queryAndStoreOLS();
			
		} catch (ExternalServiceConnectorException e) {

			e.printStackTrace();
		}
		
		// Query and store all Cellosaurus terms...
		
		
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
		
		List<EbiOlsTerm> children = pr.findByParent(parent);
		for (Iterator<EbiOlsTerm> i = children.iterator(); i.hasNext();) {
			EbiOlsTerm item = i.next();
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
    public String suggest(@RequestParam(value="label", defaultValue="extra") String label) {      
		
		//Add JSON wrapper (same for one OLS) -- how to handle different OLS-es here??
    	String returnstring = "{\"@context\": { \"GOCellComp\": \"http://purl.obolibrary.org/obo/GO_0005575\" }," + 
    			"\""+label+"\": [ ";
		
		List<EbiOlsTerm> labels = pr.findBySynonym(label);
		for (Iterator<EbiOlsTerm> i = labels.iterator(); i.hasNext();) {
			EbiOlsTerm item = i.next();
			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
			returnstring = returnstring + ",";
		} 
    	
		// remove last comma
		returnstring = returnstring.substring(0, returnstring.length()-1);
		
    	//Add end of JSON wrapper
		returnstring = returnstring + " ] }";
		
    	return returnstring;
    	
    }

}

