# PharmaJSON-LD

## Suggest method result
The SuggestQuery part holds the queried IRI, the SuggestResult has the suggested terms

```
{
  "SuggestQuery": {"skos:exactMatch": "IRI00001"},  
  "SuggestResult": [
    {
      "skos:exactMatch": "IRI00002",
      "skos:prefLabel": { "@value": "Corvus", "@language": "lat"},
      "skos:broader": { "@value": "Birds", "@language": "eng"}
    },
    {
      "skos:exactMatch": "IRI00003",
      "skos:prefLabel": { "@value": "Birds", "@language": "eng"},
      "skos:broader": { "@value": "Animals", "@language": "eng"}
    }
  ]
}
```


## GetChildren method result
Same structure as above.
```
{
  "GetChildrenQuery": {"skos:exactMatch": "IRI00002"},  
  "GetChildrenResult": [
    {
      "skos:exactMatch": "IRI00001",
      "skos:prefLabel": { "@value": "Crow", "@language": "eng"},
      "rdfs:label": { "@value": "Rabe", "@language": "deu"},
      "skos:broader": { "@value": "Corvus", "@language": "lat"}
    },
    {
      "skos:exactMatch": "IRI00004",
      "skos:prefLabel": { "@value": "Hooded Crow", "@language": "eng"},
      "skos:broader": { "@value": "Corvus", "@language": "lat"}
    },
    {
      "skos:exactMatch": "IRI00005",
      "skos:prefLabel": { "@value": "Indian Crow", "@language": "eng"},
      "skos:broader": { "@value": "Corvus", "@language": "lat"}
    }
  ]
}
```
