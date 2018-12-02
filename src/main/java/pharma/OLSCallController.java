package pharma;


import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pharma.Connector.ChebiConnector;
import pharma.Connector.EbiOlsConnector;
import pharma.Connector.ExternalServiceConnector;
import pharma.Connector.MondoConnector;
import pharma.Connector.NcbiTaxonConnector;
import pharma.Connector.OboNcitConnector;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.ChebiRepository;
import pharma.Repository.EbiOlsRepository;
import pharma.Repository.MondoRepository;
import pharma.Repository.NcbiTaxonRepository;
import pharma.Repository.OboNcitRepository;
import pharma.Term.AbstractTerm;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class OLSCallController {

	@Autowired
	private EbiOlsRepository ebiOlsRepo;
	
	@Autowired
	private OboNcitRepository oboNcitRepo;
	
	@Autowired
	private ChebiRepository chebiRepo;
	
	@Autowired
	private NcbiTaxonRepository ncbiTaxonRepo;
	
	@Autowired
	private MondoRepository mondoRepo;
	
	private EbiOlsConnector ebiOlsConn;
	private OboNcitConnector oboNcitConn;
	private ChebiConnector chebiConn;
	private MondoConnector mondoConn;
	private NcbiTaxonConnector ncbiTaxonConn;
	
	
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
    	

		try {
			
			ebiOlsConn = new EbiOlsConnector();
			oboNcitConn = new OboNcitConnector();
			mondoConn = new MondoConnector();
			ncbiTaxonConn = new NcbiTaxonConnector();
			chebiConn = new ChebiConnector();
			
			// Fetch GO terms
			updateParentPath(ebiOlsConn, "GO:0003674", ebiOlsRepo, "GO0003674");
			updateParentPath(ebiOlsConn, "GO:0008150", ebiOlsRepo, "GO0008150");
			updateParentPath(ebiOlsConn, "GO:0005575", ebiOlsRepo, "GO0005575");
			
			// Fetch NCIT terms
			updateParentPath(oboNcitConn, "NCIT:C12219", oboNcitRepo, "C12219");
			/** Iri-s are not found, default "Rat esophagus comes instead **/
			//updateParentPath(oboNcitConn, "NCIT:C16847", oboNcitRepo, "C16847");
			//updateParentPath(oboNcitConn, "NCIT:C19160", oboNcitRepo, "C19160");
			
			/*** Not yet implemented ***/
			// Fetch Mondo terms
			//updateParentPath(mondoConn, null, mondoRepo);
			
			// Fetch NcbiTaxon terms
			//updateParentPath(ncbiTaxonConn, null, ncbiTaxonRepo);
			
			// Fetch ChebiTerms
			//updateParentPath(chebiConn, null, chebiRepo);
			
		
		} catch (Exception e) {

			e.printStackTrace();
			return "{ \"updateStatus\": \"failed\"}";
		}
		// Report Success.
		return "{ \"updateStatus\": \"success\"}";
    }	


    public String updateParentPath(ExternalServiceConnector esc, String classParentTerm, Object repo, String ontoClass) {
    	
		HashMap<String, String> urlsOTermParents = new HashMap<String, String>();
		
		try {
		
			if(classParentTerm != null)
				esc.setIri(classParentTerm);
			esc.setRepo(repo);
			urlsOTermParents.putAll(esc.queryAndStoreOLS(ontoClass));
		
			for(Entry<String, String> entry : urlsOTermParents.entrySet()) {
				System.out.println("getParentByURL: "+entry.getValue()+" - "+entry.getKey());
				esc.linkParents(entry.getValue(), entry.getKey());
			}
		
		
		} catch (ExternalServiceConnectorException e) {

			e.printStackTrace();
			return "{ \"updateStatus\": \"Update parent path ("+esc.toString()+" / "+classParentTerm+") failed\"}";
		}
		// Report Success.
		return "{ \"updateStatus\": \"Update parent path ("+esc.toString()+" / "+classParentTerm+") success\"}";
    	
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
    public String getChildren(
    		@RequestParam(value="parent", defaultValue="C60743") String parent,
    		@RequestParam(value="ontology", defaultValue="ncit") String ontology,
    		@RequestParam(value="class", defaultValue="") String ontoClass) {
 	
    	// create a Set to uniquely store the found parents
    	Set<AbstractTerm> parents = new HashSet<AbstractTerm>();
    	List<AbstractTerm> parent_ids = new ArrayList<AbstractTerm>();
    	
    	// String for the response JSON
    	String returnString = "{ \"getChildrenResult\": [ "; 
    	
    	switch(ontology) {
    	case "go":
    		parents.addAll(ebiOlsRepo.findByIri(parent));
    		System.out.println("Parent IRI: "+parent);
    		for(AbstractTerm t : parents) {
    			parent_ids = ebiOlsRepo.findByParent(t, ontoClass);
    		}
    		break;
    	case "ncit":
    		parents.addAll(oboNcitRepo.findByIri(parent));
    		System.out.println("Parent IRI: "+parent);
    		for(AbstractTerm t : parents) {
    			parent_ids = oboNcitRepo.findByParent(t, ontoClass);
    		}
    		break;
    	case "ncbitaxon":
    		parents.addAll(ncbiTaxonRepo.findByIri(parent));
    		System.out.println("Parent IRI: "+parent);
    		for(AbstractTerm t : parents) {
    			parent_ids = ncbiTaxonRepo.findByParent(t);
    		}
    		break;
    	case "mondo":
    		parents.addAll(mondoRepo.findByIri(parent));
    		System.out.println("Parent IRI: "+parent);
    		for(AbstractTerm t : parents) {
    			parent_ids = mondoRepo.findByParent(t);
    		}
    		break;
    	case "chebi":
    		parents.addAll(chebiRepo.findByIri(parent));
    		System.out.println("Parent IRI: "+parent);
    		for(AbstractTerm t : parents) {
    			parent_ids = chebiRepo.findByParent(t);
    		}
    		break;
    	default: return "{error: \"Ontology "+ontology+" not supported.\"}";
    	}
    	
		int elemCount = 1;
		for(AbstractTerm t : parent_ids) {
			returnString = returnString + t.toJSON().toString();
			elemCount++;
			if(elemCount <= parent_ids.size())
				returnString = returnString + ", ";
		}
    	
    	returnString = returnString + "] }";
    	
    	return returnString;
    	
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
    		@RequestParam(value="ontology", defaultValue="go") String ontology,
    		@RequestParam(value="class", defaultValue="") String ontClass) {      
		
    	String returnstring = "{\"@context\": { \"GOCellComp\": \"http://purl.obolibrary.org/obo/GO_0005575\" }," + 
    			"\""+label+"\": [ ";
	
    	
    	switch(ontology) {
    	case "go":
    		List<AbstractTerm> labelsGo;
    		System.out.println("OC: "+ontClass);
    		labelsGo = ebiOlsRepo.findBySynonym(label, ontClass);
    		for (Iterator<AbstractTerm> i = labelsGo.iterator(); i.hasNext();) {
    			AbstractTerm item = i.next();
    			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
    			returnstring = returnstring + ",";
    		}     		
    		break;
    	case "ncit": 
    		List<AbstractTerm> labelsNcit;
    		labelsNcit = oboNcitRepo.findBySynonym(label, ontClass);
    		for (Iterator<AbstractTerm> i = labelsNcit.iterator(); i.hasNext();) {
    			AbstractTerm item = i.next();
    			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
    			returnstring = returnstring + ",";
    		}     		
    		break;
    	case "mondo":
    		break;
    	case "ncbitaxon":
    		break;
    	case "chebi":
    		break;
    	default: return "{error: \"Ontology "+ontology+" not supported.\"}";
    	}
		
    	
		// remove last comma
		returnstring = returnstring.substring(0, returnstring.length()-1);
		
    	//Add end of JSON wrapper
		returnstring = returnstring + " ] }";
		
    	return returnstring;
    	
    }

}

