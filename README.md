# PharmaJSON-LD

This is a simple REST Service to
- fetch data from an external service (multiple can be configured)
- Build entities and persist them
- Gives stored data back as JSON-LD with the /suggest and /getchildren methods

It is only the core functionality, the fetched data and its fields are not final yet

## How does it work

1. Download the repo
2. cd into "PharmaJSON-LD"
3. ./gradlew bootRun
4. Go into src/main/resources/application.properties and see the database connection details. To get the demo work you should create a database that can be accessed like its written in the application.properties.
5. Run http://localhost:8080/update - this will run a hardcoded update that fetches 2 terms from the EBI OLS
6. Try for example http://localhost:8080/suggest?label=mem where the iri parameter refers to an IRI used by the OLS (and it is also the persisted entity's UID in the DB)


# Work in progress

The responses should look like the two sections below; but for now it is not yet clear how many terms sould be returned for one query and also which terms we need to store.

The /update method (without parameters) should fetch all the terms we are using in the FE and persist them with all the necessary fileds. This method is not yet implemented.

## ToDo-s
- create a High-level diagram of the full project and process to visualize the components and their responsibilites
- prepare dummy data and examples
- integrating arbitrary outside sources
- helper scripts to initialize the DB, fetching data based on a list
- deployment questions


## Suggest method result
The SuggestQuery part holds the queried IRI, the SuggestResult has the suggested terms

```
{
  "extra": [
    {
      "skos:prefLabel": [
        {
          "@value": "extracellular organelle",
          "@language": "eng"
        }
      ],
      "skos:exactMatch": "http://purl.obolibrary.org/obo/GO_0043230",
      "rdfs:label": [
        {
          "@value": "extracellular organelle",
          "@language": "eng"
        }
      ],
      "@type": "skos:Concept"
    }
  ],
  "@context": {
    "GOCellComp": "http://purl.obolibrary.org/obo/GO_0005575"
  }
}
```


## GetChildren method result
Roughly the same structure as above.
```
Exact contents and structure TBA
```
