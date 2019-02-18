package pharma;


import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.io.IOException;
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
import pharma.Connector.UniprotConnector;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.AbstractRepository;
import pharma.Repository.ChebiRepository;
import pharma.Repository.EbiOlsRepository;
import pharma.Repository.MondoRepository;
import pharma.Repository.NcbiTaxonRepository;
import pharma.Repository.OboNcitRepository;
import pharma.Repository.UniprotRepository;
import pharma.Term.AbstractTerm;

import org.json.JSONArray;
import org.json.JSONObject;
//Import log4j classes.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Autowired
	private UniprotRepository uniprotRepo;
	
	private EbiOlsConnector ebiOlsConn;
	private OboNcitConnector oboNcitConn;
	private ChebiConnector chebiConn;
	private MondoConnector mondoConn;
	private NcbiTaxonConnector ncbiTaxonConn;
	private UniprotConnector uniprotConn;
	
	
    private static final Logger logger = LoggerFactory.getLogger(OLSCallController.class);
	
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
    	
		logger.info("Update called.");
		
		Properties prop = new Properties();
		try {
		    //load a properties file from class path, inside static method
		    prop.load(Application.class.getClassLoader().getResourceAsStream("application.properties"));

		} 
		catch (IOException ex) {
		    ex.printStackTrace();
		}
		

		try {
			
			ebiOlsConn = new EbiOlsConnector();
			oboNcitConn = new OboNcitConnector();
			mondoConn = new MondoConnector();
			ncbiTaxonConn = new NcbiTaxonConnector();
			chebiConn = new ChebiConnector();
			uniprotConn = new UniprotConnector();
			
			// Fetch GO terms
			logger.info("Fetching EBI OLS terms...");
			updateParentPath(ebiOlsConn, prop.getProperty("ebiols1"), ebiOlsRepo, prop.getProperty("ebiols1").replaceAll(":", ""));
			updateParentPath(ebiOlsConn, prop.getProperty("ebiols2"), ebiOlsRepo, prop.getProperty("ebiols2").replaceAll(":", ""));
			updateParentPath(ebiOlsConn, prop.getProperty("ebiols3"), ebiOlsRepo, prop.getProperty("ebiols3").replaceAll(":", ""));
			updateParentPath(ebiOlsConn, prop.getProperty("ebiols4"), ebiOlsRepo, prop.getProperty("ebiols4").replaceAll(":", ""));
			updateParentPath(ebiOlsConn, prop.getProperty("ebiols5"), ebiOlsRepo, prop.getProperty("ebiols5").replaceAll(":", ""));
			
			// Fetch NCIT terms
			logger.info("Fetching OBO NCIT terms...");
			updateParentPath(oboNcitConn, prop.getProperty("oboncit1"), oboNcitRepo, prop.getProperty("oboncit1").replaceAll(":", ""));
			updateParentPath(oboNcitConn, prop.getProperty("oboncit2"), oboNcitRepo, prop.getProperty("oboncit2").replaceAll(":", ""));
			updateParentPath(oboNcitConn, prop.getProperty("oboncit3"), oboNcitRepo, prop.getProperty("oboncit3").replaceAll(":", ""));
			updateParentPath(oboNcitConn, prop.getProperty("oboncit4"), oboNcitRepo, prop.getProperty("oboncit4").replaceAll(":", ""));
			updateParentPath(oboNcitConn, prop.getProperty("oboncit5"), oboNcitRepo, prop.getProperty("oboncit5").replaceAll(":", ""));
			
			// Fetch Mondo terms
			logger.info("Fetching MONDO terms...");
			updateParentPath(mondoConn, prop.getProperty("mondo1"), mondoRepo, "MONDO");
			updateParentPath(mondoConn, prop.getProperty("mondo2"), mondoRepo, "MONDO");
			updateParentPath(mondoConn, prop.getProperty("mondo3"), mondoRepo, "MONDO");
			updateParentPath(mondoConn, prop.getProperty("mondo4"), mondoRepo, "MONDO");
			updateParentPath(mondoConn, prop.getProperty("mondo5"), mondoRepo, "MONDO");
			
			// Fetch NcbiTaxon terms
			logger.info("Fetching NCBI TAXON terms...");
			updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon1"), ncbiTaxonRepo, "NCBITAXON");
			updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon2"), ncbiTaxonRepo, "NCBITAXON");
			updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon3"), ncbiTaxonRepo, "NCBITAXON");
			updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon4"), ncbiTaxonRepo, "NCBITAXON");
			updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon5"), ncbiTaxonRepo, "NCBITAXON");
			
			// Fetch ChebiTerms
			logger.info("Fetching CHEBI terms...");
			updateParentPath(chebiConn, prop.getProperty("chebi1"), chebiRepo, "CHEBI");
			updateParentPath(chebiConn, prop.getProperty("chebi2"), chebiRepo, "CHEBI");
			updateParentPath(chebiConn, prop.getProperty("chebi3"), chebiRepo, "CHEBI");
			updateParentPath(chebiConn, prop.getProperty("chebi4"), chebiRepo, "CHEBI");
			updateParentPath(chebiConn, prop.getProperty("chebi5"), chebiRepo, "CHEBI");
			
			// Fetch Uniprot terms
			logger.info("Fetching UniProt terms...");
			updateParentPath(uniprotConn, prop.getProperty("uniprot"), uniprotRepo, "UNIPROT");
		
		} catch (Exception e) {

			e.printStackTrace();
			return "{ \"updateStatus\": \"failed\"}";
		}
		// Report Success.
		return "{ \"updateStatus\": \"success\"}";
    }	


    public void updateParentPath(ExternalServiceConnector esc, String classParentTerm, Object repo, String ontoClass) {
    	
    	logger.info(" Updating " + classParentTerm + " in " + ontoClass);
    	
    	if(classParentTerm.isEmpty() || classParentTerm == null) {
    		logger.warn("Class parent term is empty, exiting.");
    		return;
    	}
    	
		HashMap<String, String> urlsOTermParents = new HashMap<String, String>();
		
		try {
		
			if(classParentTerm != null)
				esc.setIri(classParentTerm);
			esc.setRepo(repo);
			urlsOTermParents.putAll(esc.queryAndStoreOLS(ontoClass));
		
			for(Entry<String, String> entry : urlsOTermParents.entrySet()) {
				logger.info("GetParentByURL: "+entry.getValue()+" - "+entry.getKey());
				esc.linkParents(entry.getValue(), entry.getKey());
			}
		
		
		} catch (ExternalServiceConnectorException e) {

			e.printStackTrace();
			logger.warn("Update failed.");
			return;
		}
		// Report Success.
		logger.info("Update successful.");
		return;
    	
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
    	
    	JSONObject returnObject = new JSONObject();
    	JSONArray childrenArray = new JSONArray();    	
    	
    	AbstractRepository repo;
    	
    	switch(ontology) {
    	case "go":
    		repo = ebiOlsRepo;
    		break;
    	case "ncit":
    		repo = oboNcitRepo;
    		break;
    	case "ncbitaxon":
    		repo = ncbiTaxonRepo;
    		break;
    	case "mondo":
    		repo = mondoRepo; 
    		break;
    	case "chebi":
    		repo = chebiRepo;
    		break;
    	default: 
    		logger.warn("Ontology "+ontology+" not supported.");
    		return "{error: \"Ontology "+ontology+" not supported.\"}";
    	}
    	
		List<AbstractTerm> children = new ArrayList<AbstractTerm>();
		
		if(ontoClass.isEmpty()) {
			children = repo.findByParent(parent);
		} else {
			children = repo.findByParent(parent, ontoClass);
		}

		for(AbstractTerm t : children) {
			childrenArray.put(t.toJSON());
		}   	
    	
    	returnObject.append("getChildrenResult", childrenArray);
    	
    	return returnObject.toString();
    	
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
		
    	JSONObject returnObject = new JSONObject();
    	JSONArray suggestArray = new JSONArray();
		
    	AbstractRepository repo;
    	
    	switch(ontology) {
    	case "go":
    		repo = ebiOlsRepo;
    		break;
    	case "ncit":
    		repo = oboNcitRepo;
    		break;
    	case "ncbitaxon":
    		repo = ncbiTaxonRepo;
    		break;
    	case "mondo":
    		repo = mondoRepo;
    		break;
    	case "chebi":
    		repo = chebiRepo;
    		break;
    	default: 
    		logger.warn("Ontology "+ontology+" not supported.");
    		return "{error: \"Ontology "+ontology+" not supported.\"}";
    	}		
		
    	logger.info("Suggest called: " + label + " / "+ ontology + " / " + ontClass);
    	
    	returnObject.append("@context", ontology);
    	if(!ontClass.isEmpty())
			returnObject.append("class", ontClass);
    	
    	List<AbstractTerm> hits = new ArrayList<AbstractTerm>();
    	
    	if(!ontClass.isEmpty())
    		hits = repo.findBySynonym(label, ontClass);
    	else
    		hits = repo.findBySynonym(label);
    	
		for(AbstractTerm t : hits) {
			suggestArray.put(t.toJSON());
			logger.info("Suggest hit: " + t.getIri());
		}    	
    	
    	returnObject.append(label, suggestArray);
    	
    	return returnObject.toString();
    	
    }

}

