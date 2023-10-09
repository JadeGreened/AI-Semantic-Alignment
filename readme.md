# LLMA Dialogue Model experiment

This reposotory stores an implementation of the LLMA Dialogue Model for experiment purpose. The paper of LLMA Dialogue Model can be found here.

The implementation is a Java project using Maven and [MELT](https://dwslab.github.io/melt/)

## Structure
Implementation is stored in simpleSealsMatcher folder. A copy of the used OAEI dataset [Anatomy](http://oaei.ontologymatching.org/2023/anatomy/index.html) is stored in the .*/java/DataSet/ folder.

Results we retrieved and reported in paper is in the results folder. Note, we didn't use MELT generated statics, which you may find in the performance csv, as we found problems the toolkit handling our data. For example, one reference alignment between "http://human.owl#NCI_C12499" and "http://mouse.owl#MA_0000237", our exact alignment was evaluated as "false positive".

## Implementation Files
Note: This project is started for experiments. Though we tried to clean codes, you may still find testing purpose codes.

The project entrance is in Main class, and other class work as following:
* MyMatcher is the matcher implemented using MELT. It's used as the entrance of the model.
* OntologyAgent is the agent class negotiating in the model. It contains functions agents need to negotiate and get correspondence.
* OpenAI is responsible for all API calls and prompt assembly that agents would need.
* Weaviate is the vector database used by agent.
