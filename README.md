# PharmaJSON-LD

This is a simple REST Service to
- fetch data from an external service (multiple can be configured)
- Build entities and persist them
- Gives stored data back as JSON-LD with the /suggest and /getchildren methods

It is only the core functionality, the fetched data and its fields are not final yet

## How does it work

**Preparations:**
1. Download Neo4J: https://neo4j.com/download/
2. Download the APOC plugin for Neo4J from here: https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/tag/3.5.0.3 and place the .jar file into the Neo4J installation's /plugin folder
3. Start the database engine by running ./neo4j console from the downloaded program's /bin folder
4. Make sure that a database connections is available as described in the **application.properties** file 

**Running the service**

1. Download or clone the repo
2. cd into "PharmaJSON-LD"
3. **./gradlew bootRun**
4. Go into src/main/resources/**application.properties** and see the database connection details. To get the demo work you should create a database that can be accessed like its written in the application.properties.
5. Cofigure the basic terms in the application.properties file.
6. Run **http://localhost:9999/update** - this will run an update that fetches terms from the ontologies defined in the config file.
7. Try for example http://localhost:9999/suggest?label=mem&ontology=go
 where the iri parameter refers to an IRI used by the OLS (and it is also the persisted entity's UID in the DB)

The service logs its actions and errors, the logifle is called: pharma.log and is in the project's root directory.

# Supported methods

## UPDATE 
**update()**

Goes through the specified IRI-s from the application.properties file and saves recursively all their children into the database under the given ontology and class.

## SUGGEST 
**suggest(label (string), ontology ({GO, NCIT, CHEBI, NCBITAXON, MONDO, UNIPROT, BAO, CELLOSAURUS}), ontology class (string))**

returns all the terms from a given ontology's given class that contain the label parameter a a substring.

## GETCHILDREN 
**getChildren(parent (IRI), ontology ({GO, NCIT, CHEBI, NCBITAXON, MONDO, UNIPROT, BAO, CELLOSAURUS}), ontology class (string))**

returns all the children of a given parent IRI from an ontology  (or its class)

## GETTREE 

**getTree(parent (IRI), ontology ({GO, NCIT, CHEBI, NCBITAXON, MONDO, UNIPROT, BAO, CELLOSAURUS}), ontology class (string), filter (string))**

returns all the descendants of a term containing the filter expression. If the filter is empty, returns all the children of children recursively

## CHECKIRI

**checkIri(iri (IRI), ontology ({GO, NCIT, CHEBI, NCBITAXON, MONDO, UNIPROT, BAO, CELLOSAURUS}))**

helper method - returns true if the queried IRI exists in the database, false otherwise.

# Request and Configuration Examples

Searching for "rat" in the labels of the NCIT terms:
http://localhost:9999/suggest?label=rat&ontology=ncit&class=NCITC:12219

Searching for "cell" in the Chebi ontology: (Note: no class is specified, because the Chebi ontology has no saved classes)
http://localhost:9999/suggest?label=cell&ontology=chebi

Listing the children of the term which has "C60743" in its IRI from the NCIT ontology's NCITC12219 class
http://localhost:9999/getchildren?parent=C60743&ontology=ncit&class=NCITC:12219

Listing the children of the term which has "01234567" in its IRI:
http://localhost:9999/getchildren?parent=01234567&ontology=mondo

Application.properties configuration for getting recursively all the child-terms of "GO:0003674" into the GO ontology's "GO0003674" class:
ebiols1=GO:0003674

## Response Examples

Suggest:
(Request: http://localhost:9999/suggest?ontology=uniprot&label=homolog)

```
{
	"@context": "uniprot",
	"homolog": [{
		"skos:prefLabel": [{
			"@value": "Uncharacterized protein C7orf57 homolog",
			"@language": "@eng"
		}],
		"@type": ["skos:Concept"],
		"@ID": ["Q5SS90"],
		"skos:altLabel": [{
			"@value": "Uncharacterized protein C7orf57 homolog",
			"@language": "@eng"
		}]
	}, {
		"skos:prefLabel": [{
			"@value": "Uncharacterized protein C7orf61 homolog",
			"@language": "@eng"
		}],
		"@type": ["skos:Concept"],
		"@ID": ["Q2T9X5"],
		"skos:altLabel": [{
			"@value": "Uncharacterized protein C7orf61 homolog",
			"@language": "@eng"
		}]
	}]
}
```

GetChildren:
(Request: http://localhost:9999/getchildren?parent=Q27081&ontology=chebi)

```
{
	"getChildrenResult": [{
		"skos:prefLabel": [{
			"@value": "G2/mitotic-specific cyclin cig2",
			"@language": "@eng"
		}],
		"@type": ["skos:Concept"],
		"@ID": ["P36630"],
		"skos:altLabel": [{
			"@value": "G2/mitotic-specific cyclin cig2",
			"@language": "@eng"
		}]
	}]
}
```

GetTree:
(Request: http://localhost:9999/gettree?parent=CVCL_0030&ontology=cellosaurus&filter=AR)

```
{"Tree":
	[{"value":
		{"_type":"AbstractTerm:CellosaurusTerm",
		 "iri":"https://web.expasy.org/cellosaurus/CVCL_0030",
		 "ontoclass":"CELLOSAURUS",
		 "_id":2095,
		 "label":"Cellosaurus cell line HeLa (CVCL_0030)",
		 "child":
		 	[
			{"_type":"AbstractTerm:CellosaurusTerm",
			 "iri":"https://web.expasy.org/cellosaurus/CVCL_3380",
			 "ontoclass":"CELLOSAURUS",
			 "_id":2134,
			 "label":"Cellosaurus cell line HeLa 422 (CVCL_3380)"},
			 ...
```

CheckIri:
(Request: http://localhost:9999/checkIri?iri=CVCL_0031&ontology=cellosaurus)

```
{"response":false}
```

Update:
(Request: http://localhost:9999/update)

```
{ "updateStatus": "success"}
```

 
