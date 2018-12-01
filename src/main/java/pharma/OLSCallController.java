package pharma;


import java.util.List;
import java.util.Map.Entry;
import java.util.HashMap;
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
			updateParentPath(ebiOlsConn, "GO:0003674", ebiOlsRepo);
			updateParentPath(ebiOlsConn, "GO:0008150", ebiOlsRepo);
			updateParentPath(ebiOlsConn, "GO:0005575", ebiOlsRepo);
			
			// Fetch NCIT terms
			updateParentPath(oboNcitConn, "NCIT:C12219", oboNcitRepo);
			updateParentPath(oboNcitConn, "NCIT:C16847", oboNcitRepo);
			updateParentPath(oboNcitConn, "NCIT:C19160", oboNcitRepo);
			
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


    public String updateParentPath(ExternalServiceConnector esc, String classParentTerm, Object repo) {
    	
		HashMap<String, String> urlsOTermParents = new HashMap<String, String>();
		
		try {
		
			if(classParentTerm != null)
				esc.setIri(classParentTerm);
			esc.setRepo(repo);
			urlsOTermParents.putAll(esc.queryAndStoreOLS());
		
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
    public String getChildren(@RequestParam(value="parent", defaultValue="C60743") String parent) {
 	
    	
    	String retrunstring = "";
		
    	List<AbstractTerm> parents = ebiOlsRepo.findByIri(parent);
		
    	for (Iterator<AbstractTerm> i = parents.iterator(); i.hasNext();) {
			AbstractTerm item = i.next();
			List<AbstractTerm> parent_ids = ebiOlsRepo.findByParent(item.getId());
			
			if(!parent_ids.isEmpty()) {
				System.out.println(parent_ids.get(0).getIri());
		    	for (Iterator<AbstractTerm> j = parent_ids.iterator(); j.hasNext();) {
		    		AbstractTerm child = j.next();
		    		retrunstring = retrunstring + System.lineSeparator() + child.toJSON().toString();
		    	}	
			}
		} 
    	   	
    	
    	
    	parents = oboNcitRepo.findByIri(parent);
		
    	for (Iterator<AbstractTerm> i = parents.iterator(); i.hasNext();) {
			AbstractTerm item = i.next();
			List<AbstractTerm> parent_ids = oboNcitRepo.findByParent(item.getId());
			
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
    		@RequestParam(value="ontology", defaultValue="go") String ontology,
    		@RequestParam(value="class", defaultValue="") String ontClass) {      
		
		//Add JSON wrapper (same for one OLS) -- how to handle different OLS-es here??
    	String returnstring = "{\"@context\": { \"GOCellComp\": \"http://purl.obolibrary.org/obo/GO_0005575\" }," + 
    			"\""+label+"\": [ ";
	
    	
    	switch(ontology) {
    	case "go":
    		List<AbstractTerm> labelsGo;
    		labelsGo = ebiOlsRepo.findBySynonym(label);
    		for (Iterator<AbstractTerm> i = labelsGo.iterator(); i.hasNext();) {
    			AbstractTerm item = i.next();
    			returnstring = returnstring + System.lineSeparator() + item.toJSON().toString();
    			returnstring = returnstring + ",";
    		}     		
    		break;
    	case "ncit": 
    		List<AbstractTerm> labelsNcit;
    		labelsNcit = oboNcitRepo.findBySynonym(label);
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

