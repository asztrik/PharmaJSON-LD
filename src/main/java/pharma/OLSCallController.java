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
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import pharma.Connector.BaoConnector;
import pharma.Connector.CellosaurusConnector;
import pharma.Connector.ChebiConnector;
import pharma.Connector.EbiOlsConnector;
import pharma.Connector.ExternalServiceConnector;
import pharma.Connector.MondoConnector;
import pharma.Connector.NcbiTaxonConnector;
import pharma.Connector.OboNcitConnector;
import pharma.Connector.UniprotConnector;
import pharma.Exception.ExternalServiceConnectorException;
import pharma.Repository.AbstractRepository;
import pharma.Repository.BaoRepository;
import pharma.Repository.CellosaurusRepository;
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

	@Autowired
	private BaoRepository baoRepo;

	@Autowired
	private CellosaurusRepository cellosaurusRepo;
	
	private EbiOlsConnector ebiOlsConn;
	private OboNcitConnector oboNcitConn;
	private ChebiConnector chebiConn;
	private MondoConnector mondoConn;
	private NcbiTaxonConnector ncbiTaxonConn;
	private UniprotConnector uniprotConn;
	private BaoConnector baoConn;
	private CellosaurusConnector cellosaurusConn;
	
	
    private static final Logger logger = LoggerFactory.getLogger(OLSCallController.class);
	
    

	@RequestMapping("/")
    public String home() {
		return 
			"<html>\n" + 
			"<body>\n" + 
			"<h1>Ontology Lookup Service </h1>\n" + 
			"<h2>Fairness for Pharma Data </h2>\n" + 
			"The service currently offers the following methods\n" + 
			"<ul>\n" + 
			"	<li>suggest</li>\n" + 
			"	<li>getChildren</li>\n" + 
			"	<li>update</li>\n" + 
			"</ul>\n" + 
			"For more info please visit the <a href=\"https://github.com/asztrik/PharmaJSON-LD\">github page</a>.\n" + 
			"</body>\n" + 
			"</html>";
	}
    
    
	/**
     * Updates terms based on the list in application.properties
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
			baoConn = new BaoConnector();
			cellosaurusConn = new CellosaurusConnector();
			
			// Fetch GO terms
			logger.info("Fetching EBI OLS terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("ebiols"+String.valueOf(i));
				updateParentPath(ebiOlsConn, prop.getProperty("ebiols"+String.valueOf(i)), ebiOlsRepo, prop.getProperty("ebiols"+String.valueOf(i)).replaceAll(":", ""));
			}			
			
			// Fetch NCIT terms
			logger.info("Fetching OBO NCIT terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("oboncit"+String.valueOf(i));
				updateParentPath(oboNcitConn, prop.getProperty("oboncit"+String.valueOf(i)), oboNcitRepo, prop.getProperty("oboncit"+String.valueOf(i)).replaceAll(":", ""));
			}			
			
			// Fetch Mondo terms
			logger.info("Fetching MONDO terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("mondo"+String.valueOf(i));
				updateParentPath(mondoConn, prop.getProperty("mondo"+String.valueOf(i)), mondoRepo, "MONDO");
			}			
			
			// Fetch NcbiTaxon terms
			logger.info("Fetching NCBI TAXON terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("ncbitaxon"+String.valueOf(i));
				updateParentPath(ncbiTaxonConn, prop.getProperty("ncbitaxon"+String.valueOf(i)), ncbiTaxonRepo, "NCBITAXON");
			}
			
			// Fetch ChebiTerms
			logger.info("Fetching CHEBI terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("chebi"+String.valueOf(i));
				updateParentPath(chebiConn, prop.getProperty("chebi"+String.valueOf(i)), chebiRepo, "CHEBI");
			}
			
			// Fetch Uniprot terms
			logger.info("Fetching UniProt terms...");
			updateParentPath(uniprotConn, prop.getProperty("uniprot"), uniprotRepo, "UNIPROT");
			
			// Fetch BAOTerms
			logger.info("Fetching BAO terms...");
			for(int i = 1; i < 18; i++) {
				logger.info("bao"+String.valueOf(i));
				updateParentPath(baoConn, prop.getProperty("bao"+String.valueOf(i)), baoRepo, prop.getProperty("bao"+String.valueOf(i)));
			}	
			
			// Fetch CellosaurusTerms
			logger.info("Fetching Cellosaurus terms...");
			for(int i = 1; i < 6; i++) {
				logger.info("cellosaurus"+String.valueOf(i));
				updateParentPath(cellosaurusConn, prop.getProperty("cellosaurus"+String.valueOf(i)), cellosaurusRepo, "CELLOSAURUS");
			}	
			
			
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
	 * Returns the terms that have the parent specified in the parameter
	 * Uses the CHILD_OF relation
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
    	case "bao":
    		repo = baoRepo;
    		break;    		
    	case "cellosaurus":
    		repo = cellosaurusRepo;
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
    		@RequestParam(value="class", defaultValue="") String ontClass,
			@RequestParam(value="limit", defaultValue="") String limit) {      
	
		
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
    	case "uniprot":
    		repo = uniprotRepo;
    		break;
    	case "bao":
    		repo = baoRepo;
    		break;
    	case "cellosaurus":
    		repo = cellosaurusRepo;
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

