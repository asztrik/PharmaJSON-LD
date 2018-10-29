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
		HashMap<String, String> urlsOfEbiOlsParents = new HashMap<String, String>();
		HashMap<String, String> urlsOfOboNcitParents = new HashMap<String, String>();
		
		try {
			
			/* now always concatenate the baselist with the returns of query&store */
			eoc = new EbiOlsConnector("GO:0005575", pr); // parent of 0043226
			urlsOfEbiOlsParents.putAll(eoc.queryAndStoreOLS());
			eoc = new EbiOlsConnector("GO:0043226", pr);
			urlsOfEbiOlsParents.putAll(eoc.queryAndStoreOLS());
			
			
			ncc = new OboNcitConnector("NCIT:C60743", nr);
			urlsOfOboNcitParents .putAll(ncc.queryAndStoreOLS());
	
		
			for(Entry<String, String> entry : urlsOfEbiOlsParents.entrySet()) {
				System.out.println("getParentByURL: "+entry.getValue()+" - "+entry.getKey());
				eoc.getParentByURL(entry.getValue(), entry.getKey());
			}

			for(Entry<String, String> entry : urlsOfOboNcitParents.entrySet()) {
				System.out.println("getParentByURL: "+entry.getValue()+" - "+entry.getKey());
				ncc.getParentByURL(entry.getValue(), entry.getKey());
			}
			
			
		
		} catch (ExternalServiceConnectorException e) {

			e.printStackTrace();
		}
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
    public String getChildren(@RequestParam(value="parent", defaultValue="C60743") String parent) {
 	
    	
    	String retrunstring = "";
		
    	List<AbstractTerm> parents = pr.findByIri(parent);
		
    	for (Iterator<AbstractTerm> i = parents.iterator(); i.hasNext();) {
			AbstractTerm item = i.next();
			List<AbstractTerm> parent_ids = pr.findByParent(item.getId());
			
			if(!parent_ids.isEmpty()) {
				System.out.println(parent_ids.get(0).getIri());
		    	for (Iterator<AbstractTerm> j = parent_ids.iterator(); j.hasNext();) {
		    		AbstractTerm child = j.next();
		    		retrunstring = retrunstring + System.lineSeparator() + child.toJSON().toString();
		    	}	
			}
		} 
    	   	
    	
    	
    	parents = nr.findByIri(parent);
		
    	for (Iterator<AbstractTerm> i = parents.iterator(); i.hasNext();) {
			AbstractTerm item = i.next();
			List<AbstractTerm> parent_ids = nr.findByParent(item.getId());
			
			if(!parent_ids.isEmpty()) {
				System.out.println(parent_ids.get(0).getIri());
		    	for (Iterator<AbstractTerm> j = parent_ids.iterator(); j.hasNext();) {
		    		AbstractTerm child = j.next();
		    		retrunstring = retrunstring + System.lineSeparator() + child.toJSON().toString();
		    	}	
			}
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
    		List<AbstractTerm> labelsNcit;
    		labelsNcit = nr.findBySynonym(label);
    		for (Iterator<AbstractTerm> i = labelsNcit.iterator(); i.hasNext();) {
    			AbstractTerm item = i.next();
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

