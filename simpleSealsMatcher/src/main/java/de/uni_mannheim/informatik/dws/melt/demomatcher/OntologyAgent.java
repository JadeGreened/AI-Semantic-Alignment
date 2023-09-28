package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.alibaba.fastjson.JSONObject;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Correspondence;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OntologyAgent {
    private OntModel ontology;
    private Zilliz db;
    private OpenAI ai;
    private Object JointKnowledgeBase;  // TODO: define later
    private boolean isFinished = false;
    private String collectionName;

    public OntologyAgent(OntModel ontology, String collectionName){
        this.ontology = ontology;
        this.collectionName = collectionName;
        this.ai = new OpenAI();
        this.db = new Zilliz(collectionName).initCollection();
        this.embeddingComponents(ontology);
    }

    private void embeddingComponents(OntModel ontology){
        List<JSONObject> rows = new ArrayList<>();
        int i = 0;
        for (OntClass ontClass : ontology.listClasses().toList()) {
            if (ontClass.getURI() == null){
                continue;
            }

            String info = "";
            info += ontClass.getLocalName() +"\n";
            info += ontClass.getLabel(null) +"\n";
            info += ontClass.getComment(null);

            JSONObject json_row = new JSONObject(1, true);

            json_row.put("vector", ai.getEmbeddings(info));
            json_row.put("uri", ontClass.getURI());

            rows.add(json_row);
            System.out.println(++i);
        }

        // write rows into local file
        try {
            FileWriter file = new FileWriter(collectionName + ".json");
            for (JSONObject jsonObject : rows) {
                file.write(jsonObject.toJSONString());
                file.write("\n");

            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // store rows into database
        // TODO: Exception in thread "main" java.lang.RuntimeException: io.milvus.exception.ParamException:
        //  Field value cannot be empty. If the field is auto-id, just ignore it from withRows()
        db.insert(rows);
    }

    public boolean isFinished(){
        return isFinished;
    }

    /***
     * Init a round of negotiation. Pick one entity that has not negotiated.
     * @return the entity for this round of negotiation. null if all entities have been negotiated.
     */
    public OntClass startNegotiation(){
        // TODO: return one entity that has not been negotiated
//        db.getOneEntityNotNegotiated();
        return null;
    }

    /***
     * Receive the entity from the other agent, and find all entities that are relevant to the given entity
     * @param entity the given entity
     * @return the set of all entities that are relevant to the given entity. null if no relevant entities.
     */
    public Set<PotentialCorrespondence> receiveNegotiation(OntClass entity) {
        // TODO: register received entity to the Joint Knowledge Base
        Set<OntClass> relevantEntities = findAllRelevantEntities(entity);
        if (relevantEntities.isEmpty()){
            return null;
        }
        Set<PotentialCorrespondence> potentialCorrespondences = examineRelevantEntities(entity, relevantEntities);
        //TODO: register the correspondences to the Joint Knowledge Base
        return potentialCorrespondences;
    }

    /***
     * Receive the correspondences with relevant entities from the other agent, and find all entities that are relevant to the given entity
     * @param entity the given entity
     * @param potentialCorrespondences the set of all correspondences with relevant entities
     * @return the set of all correspondences that are relevant to the given entity. will NOT be null.
     */
    public Set<PotentialCorrespondence> furtherNegotiation(OntClass entity, Set<PotentialCorrespondence> potentialCorrespondences){
        // TODO: register received entity to the Joint Knowledge Base
        // TODO: find all entities that are relevant to the given entity
        // TODO: examine all entities that are relevant to the given entity for correspondences
        // TODO: compare the correspondences with the received correspondences
        // TODO: register the correspondences to the Joint Knowledge Base
        return null;
    }

    /***
     * End the negotiation. Register the correspondence to the Joint Knowledge Base
     * @param potentialCorrespondence the potential agreed correspondence.
     * @return the agreed correspondence. null if no agreement.
     */
    public Correspondence endNegotiation(PotentialCorrespondence potentialCorrespondence){
        // TODO: register the correspondence to the Joint Knowledge Base
        return null;
    }

    public void Finish() {
        isFinished = true;
    }

    public void clean(){
        this.db.dropCollection();
    }

// region private methods for negotiation
    /***
     * Find all entities that are relevant to the given entity
     * @param entity the given entity
     * @return the set of all entities that are relevant to the given entity
     */
    private Set<OntClass> findAllRelevantEntities(OntClass entity){
        // TODO: return all entities that are relevant to the given entity, find it from the embedding server
        return null;
    }

    /***
     * Examine all entities that are relevant to the given entity
     * @param entity the given entity
     * @param relevantEntities the set of all entities that are relevant to the given entity
     * @return the set of all correspondences that are relevant to the given entity
     */
    private Set<PotentialCorrespondence> examineRelevantEntities(OntClass entity, Set<OntClass> relevantEntities){
        Set<PotentialCorrespondence> potentialCorrespondences = new HashSet<>();
        for (OntClass relevantEntity : relevantEntities) {
            PotentialCorrespondence potentialCorrespondence = examineRelevantEntities(entity, relevantEntity);
            if (potentialCorrespondence != null){
                potentialCorrespondences.add(potentialCorrespondence);
            }
        }
        return potentialCorrespondences;
    }

    /***
     * Examine the relevance of the given entity and the given relevant entity
     * @param entity the given entity
     * @param relevantEntity the given relevant entity
     * @return the correspondence of the given entity and the given relevant entity. If the relevance is not enough, return null
     */
    private PotentialCorrespondence examineRelevantEntities(OntClass entity, OntClass relevantEntity){
        //TODO: examine the relevance of the given entity and the given relevant entity
        return null;
    }
// endregion
}
