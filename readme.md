# LLMA Dialogue Model experiment

This reposotory stores an implementation of the LLMA Dialogue Model for experiment purpose. The paper of LLMA Dialogue Model can be found here.

The implementation is a Java project using Maven and [MELT](https://dwslab.github.io/melt/).

This project uses GPT through [Azure OpenAI](https://azure.microsoft.com/en-gb/products/ai-services/openai-service/). You will need to update model information in OpenAI.java file. The recommended model is gpt-4-32k.

## Structure
Implementation is stored in simpleSealsMatcher folder. A copy of the used OAEI dataset [Anatomy](http://oaei.ontologymatching.org/2023/anatomy/index.html) is stored in the .*/java/DataSet/ folder.

Results we retrieved and reported in paper is in the results folder. Note, we didn't use MELT generated statics, which you may find in the performance csv, as we found problems the toolkit handling our data. For example, one reference alignment between "http://human.owl#NCI_C12499" and "http://mouse.owl#MA_0000237", our exact alignment was evaluated as "false positive".

## Implementation Files
Note: This project is started for experiments. Though we tried to clean codes, you may still find testing purpose codes.

The project entrance is in Main class, and other class work as following:
* MyMatcher is the matcher implemented using MELT. It's used as the entrance of the model.
* OntologyAgent is the agent class negotiating in the model. It contains functions agents need to negotiate and get correspondence.
* OpenAI is responsible for all API calls and prompt assembly that agents would need.
* Weaviate is the vector database used by agent. For Weaviate instruction, see [here](./simpleSealsMatcher/Weaviate/readme.md)

## Paper implementation
The implementation LLMA paper used is the [v0.1.0](https://github.com/JadeGreened/AI-Semantic-Alignment/releases/tag/v0.1.0)

### Embedding information

```JAVA
info += ontClass.getLocalName() +"\n";
info += ontClass.getLabel(null) +"\n";
info += ontClass.getComment(null);
```

### Data stored in vector database

```JAVA
json_row.put("vector", ai.getEmbeddings(info));
json_row.put("uri", ontClass.getURI());
json_row.put("isNegotiated", false);
```

### Query to GPT
```
URI: <getURI()>
Label: <getLabel(null)>
Local name: <getLocalName>
Comment: <getComment>

<if(listProperty startsWith(http) and not null), then below>
Property: <getPredicate().getLocalName()>
Value: <getObject().toString()>

... More listProperties...


<sub-info if exists, then below>
================ Relevant entity of this ontology ================
Label: <getLabel(null)>
URI: <getURI()>
Local name: <getLocalName()>
Comment: <getComment(null)>
<if(listProperty startsWith(http) and not null), then below>
Property: <getPredicate().getLocalName()>
Value: <getObject().toString()>

... More listProperties...
```


## License
[MIT](LICENSE)
