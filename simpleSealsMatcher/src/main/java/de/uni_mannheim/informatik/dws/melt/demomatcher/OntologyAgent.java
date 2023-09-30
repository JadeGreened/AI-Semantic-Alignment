package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.alibaba.fastjson.JSONObject;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OntologyAgent {
    private OntModel ontology;
    private Weaviate db;
    private OpenAI ai;
    private Object JointKnowledgeBase;  // TODO: define later
    private boolean isFinished = false;
    private String collectionName;
    private static final double SIMILARITY_THRESHOLD = 0.95;

    public OntologyAgent(OntModel ontology, String collectionName, boolean conductEmbedding) {
        this.ontology = ontology;
        this.collectionName = collectionName;
        this.ai = new OpenAI();
//        this.db.initCollection();
        this.db = new Weaviate(collectionName);
    }

    public String getCollectionName() {
        return collectionName;
    }

    private void embeddingComponents(OntModel ontology){
        // TODO: rewrite this function with Weaviate
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
            json_row.put("isNegotiated", false);

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
//        db.insert(rows);
    }

    public boolean isFinished(){
        return isFinished;
    }

    /***
     * Init a round of negotiation. Pick one entity that has not negotiated.
     * @return the entity for this round of negotiation. null if all entities have been negotiated.
     */
    public OntClass startNegotiation(){
        String uri = db.getUriForNotNegotiated();
        if (uri.isEmpty()){
            return null;
        }
        return ontology.getOntClass(uri);
    }

    public ArrayList<Double> getEmbedding(OntClass entity){
        String uri = entity.getURI();
        return db.getEmbedding(uri);
    }

    /***
     * Receive the entity from the other agent, and find all entities that are relevant to the given entity
     * @param entity the given entity
     * @param embedding embedding of the given entity
     * @return the set of all entities that are relevant to the given entity. null if no relevant entities.
     */
    public Set<PotentialCorrespondence> proposeCorrespondence(OntClass entity, ArrayList<Double> embedding) {
        Set<OntClass> relevantEntities = findAllRelevantNotNegotiatedEntities(embedding);
        if (relevantEntities == null){
            return null;
        }
        // TODO: register received entity to the Joint Knowledge Base

        Set<PotentialCorrespondence> potentialCorrespondences = examineRelevantEntities(entity, relevantEntities);
        //TODO: register the correspondences to the Joint Knowledge Base
        return potentialCorrespondences;
    }

    /***
     * Receive the correspondences with relevant entities from the other agent, and find all entities that are relevant to the given entity
     * @param theEntity the source entity of the negotiation
     * @param potentialCorrespondences the set of all correspondences with relevant entities. The source entity is the original one to align.
     * @return the selected correspondence. will NOT be null.
     */
    public PotentialCorrespondence checkProposal(OntClass theEntity, Set<PotentialCorrespondence> potentialCorrespondences, PotentialCorrespondence betterCorrespondence, OntologyAgent otherAgent){
        // TODO: register received entity to the Joint Knowledge Base


//        checkAllPotentialCorrespondencesWithSelfEntities(potentialCorrespondences);
        // TODO: register the correspondences to the Joint Knowledge Base
        return null;
    }

    private void checkAllPotentialCorrespondencesWithSelfEntities(Set<PotentialCorrespondence> potentialCorrespondences) {
        // check if entities in newly added correspondences have better alignment
        for (PotentialCorrespondence potentialCorrespondence: potentialCorrespondences){
            // if it has been examined
            if (potentialCorrespondence.isExamined()){
                continue;
            }
            // if it was generated by this agent
            if (potentialCorrespondence.getGenerator() == this){
                continue;
            }

            // find potential alignment for the target entity of correspondence
            Set<PotentialCorrespondence> proposedCorrespondencesOfTarget = this.proposeCorrespondence(potentialCorrespondence.getTarget(), getEmbedding(potentialCorrespondence.getTarget()));

            if (proposedCorrespondencesOfTarget == null){
                potentialCorrespondence.setExamined();
                continue;
            }

            // ask GPT which one is better for the target
            String[] newCorrespondences = new String[proposedCorrespondencesOfTarget.size()];
            int i = 0;
            for (PotentialCorrespondence correspondence : proposedCorrespondencesOfTarget) {
                newCorrespondences[i++] = toStringForGPT(correspondence.getTarget());
            }
            i = ai.whichComponentIsBetter(toStringForGPT(potentialCorrespondence.getTarget()), newCorrespondences, 0);

            if (i > -1){    // if there's a result
                if (i < potentialCorrespondences.size()) {   // if the result is in the range of the received correspondences
                    // TODO: find which entity it is
                    // TODO: check if the entity is theEntity
                    // TODO: if yes, agree the correspondence
                    // TODO: if not, disagree the correspondence and propose
                    //  a) the better choice of the new one,
                    //  and b)
                }
            }
        }

        // examine all entities that are in newly added correspondences
        // TODO: compare the correspondences with the received correspondences
    }

    /***
     * Find which one from the proposed correspondences is better as an alignment for the given entity
     * @param entity the given entity
     * @param proposedCorrespondences the set of all proposed correspondences
     * @return the selected correspondence. Null if something wrong.
     */
    public PotentialCorrespondence whichTargetIsBetter(OntClass entity, Set<PotentialCorrespondence> proposedCorrespondences){
        String[] newCorrespondences = new String[proposedCorrespondences.size()];
        OntClass[] newCorrespondencesEntities = new OntClass[proposedCorrespondences.size()];
        int i = 0;
        for (PotentialCorrespondence correspondence : proposedCorrespondences) {
            newCorrespondencesEntities[i] = correspondence.getTarget();
            newCorrespondences[i++] = toStringForGPT(correspondence.getTarget());
        }
        i = ai.whichComponentIsBetter(toStringForGPT(entity), newCorrespondences, 0);

        if (i < 0){
            return null;
        }
        if (i >= proposedCorrespondences.size()) {
            return null;
        }

        return new PotentialCorrespondence(entity, newCorrespondencesEntities[i], this);
    }

    public PotentialCorrespondence whichTargetIsBetter(OntClass entity, Set<PotentialCorrespondence> proposedCorrespondences, OntClass betterCorrespondenceEntity){
        String[] newCorrespondences = new String[proposedCorrespondences.size()];
        OntClass[] newCorrespondencesEntities = new OntClass[proposedCorrespondences.size()];
        int i = 0;
        int beliefIndex = 0;
        for (PotentialCorrespondence correspondence : proposedCorrespondences) {
            newCorrespondencesEntities[i] = correspondence.getTarget();
            newCorrespondences[i++] = toStringForGPT(correspondence.getTarget());
            if (betterCorrespondenceEntity == correspondence.getTarget()){
                beliefIndex = i;
            }
        }
        i = ai.whichComponentIsBetter(toStringForGPT(entity), newCorrespondences, beliefIndex);

        if (i < 0){
            return null;
        }
        if (i >= proposedCorrespondences.size()) {
            return null;
        }

        return new PotentialCorrespondence(entity, newCorrespondencesEntities[i], this);
    }


    /***
     * End the negotiation. Register the correspondence to the Joint Knowledge Base
     * @param potentialCorrespondence the potential agreed correspondence.
     */
    public void endNegotiation(PotentialCorrespondence potentialCorrespondence){
        // TODO: register the correspondence to the Joint Knowledge Base

        db.markNegotiated(potentialCorrespondence.getSource().getURI(), potentialCorrespondence.getTarget().getURI());
    }

    public void Finish() {
        isFinished = true;
    }

    public void clean(){
//        this.db.dropCollection();
    }

    /***
     * Find all entities that are relevant to the given entity
     * @param embedding embedding of the given entity
     * @return the set of all entities that are relevant to the given entity. Null if no relevant entities.
     */
    private Set<OntClass> findAllRelevantNotNegotiatedEntities(ArrayList<Double> embedding){
        ArrayList<String> uris = db.getUrisNotNegotiated(embedding, SIMILARITY_THRESHOLD);
        if (uris == null){
            return null;
        }
        HashSet<OntClass> relevantEntities = new HashSet<>();
        for(String uri : uris){
            relevantEntities.add(ontology.getOntClass(uri));
            System.out.println(collectionName + " find a entity based on embedding: " + ontology.getOntClass(uri).getLabel(null));
        }
        if(relevantEntities.isEmpty()){
            return null;
        }
        return relevantEntities;
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
            PotentialCorrespondence potentialCorrespondence = examineRelevantEntity(entity, relevantEntity);
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
    private PotentialCorrespondence examineRelevantEntity(OntClass entity, OntClass relevantEntity){
        String entityString = toStringForGPT(entity);
        String relevantEntityString = toStringForGPT(relevantEntity);

        boolean result = ai.comepareComponenties(entityString, relevantEntityString);
        if (result){
            System.out.println(collectionName + " examine embedding and find a entity for potential correspondence: " + relevantEntity.getLabel(null));
            return new PotentialCorrespondence(entity, relevantEntity, this);
        }
        return null;
    }

    private String toStringForGPT(OntClass ontClass){
        String info = "";
        String uri = ontClass.getURI();
        info += "Class  URI: " + uri + "\n";

        // 获取并打印类的标签
        String label = ontClass.getLabel(null);
//        System.out.println("Label: " + label);
        info += "Label: " + label + "\n";
        //所有的属性
        for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
            Statement stmt = i.next();
            info += "Property: " + stmt.getPredicate().getLocalName() + "\n";
            info += "Value: " + stmt.getObject().toString() + "\n";
        }
        return info;
    }
}






















