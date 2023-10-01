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

    /***
     * Find which one from the proposed correspondences is better as an alignment for the given entity
     * @param entity the given entity
     * @param proposedCorrespondences the set of all proposed correspondences
     * @param betterCorrespondenceEntity the entity that the other agent believes is the best choice aligning to the given entity
     * @return the selected correspondence. Null if something wrong.
     */
    public PotentialCorrespondence whichTargetIsBetter(OntClass entity, Set<PotentialCorrespondence> proposedCorrespondences, OntClass betterCorrespondenceEntity){
        String[] correspondences = new String[proposedCorrespondences.size()];
        OntClass[] newCorrespondencesEntities = new OntClass[proposedCorrespondences.size()];
        int i = 0;
        int beliefIndex = 0;
        for (PotentialCorrespondence correspondence : proposedCorrespondences) {
            newCorrespondencesEntities[i] = correspondence.getTarget();
            correspondences[i++] = toStringForGPT(correspondence.getTarget());
            if (betterCorrespondenceEntity == correspondence.getTarget()){
                beliefIndex = i;
            }
        }

        String[] relevantEntities = null;
        if (betterCorrespondenceEntity != null){
            // find all entities that are relevant to the given entity
            Set<OntClass> relevantEntitiesSet = findAllRelevantNotNegotiatedEntities(getEmbedding(entity));
            // push entities to string array
            if (relevantEntitiesSet != null){
                relevantEntities = new String[relevantEntitiesSet.size()];
                i = 0;
                for (OntClass relevantEntity : relevantEntitiesSet) {
                    relevantEntities[i++] = toStringForGPT(relevantEntity);
                }
            }
        }

        i = ai.whichComponentIsBetter(toStringForGPT(entity), correspondences, beliefIndex, relevantEntities);

        if (i < 0){
            return null;
        }
        if (i >= proposedCorrespondences.size()) {
            return null;
        }

        return new PotentialCorrespondence(entity, newCorrespondencesEntities[i], this);
    }

    public void test(){

//        // TODO: find relevant entities of the given entities
//        Set<PotentialCorrespondence> relevantCorrespondencesForEntity = proposeCorrespondence(entity, getEmbedding(entity));
//        String[] relevantCorrespondences = new String[relevantCorrespondencesForEntity.size()];
//        i = 0;
//        for (PotentialCorrespondence correspondence : relevantCorrespondencesForEntity) {
//            relevantCorrespondences[i++] = toStringForGPT(correspondence.getTarget());
//        }
    }

    public void markNegotiated(OntClass entity){
        db.markNegotiated(entity.getURI());
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
        String logger = "";
        for(String uri : uris){
            OntClass tmp = ontology.getOntClass(uri);
            if(tmp == null){    // where there is vect
                continue;
            }
            relevantEntities.add(tmp);
            logger += tmp.getLabel(null) + ", ";
        }
        System.out.println(collectionName + " find entities based on embedding: " + logger);
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

        OntClass[] relevantEntitiesArray = new OntClass[relevantEntities.size()];
        int j = 0;
        for (OntClass relevantEntity : relevantEntities) {
            relevantEntitiesArray[j++] = relevantEntity;
        }
        String[] relevantEntitiesString = new String[relevantEntities.size()];
        for (int i = 0; i < relevantEntities.size(); i++) {
            relevantEntitiesString[i] = toStringForGPT(relevantEntitiesArray[i]);
        }
        int[] results = ai.comepareComponenties(toStringForGPT(entity), relevantEntitiesString);
        String logger = "";
        for (int i = 0; i < results.length; i++) {
            if (results[i] < 0 || results[i] >= relevantEntities.size()){
                continue;
            }
            potentialCorrespondences.add(new PotentialCorrespondence(entity, relevantEntitiesArray[results[i]], this));
            logger += relevantEntitiesArray[results[i]].getLabel(null) + ", ";
        }
        System.out.println(collectionName + " examine embedding and find entities for potential correspondence: " + logger);

//        for (OntClass relevantEntity : relevantEntities) {
//            PotentialCorrespondence potentialCorrespondence = examineRelevantEntity(entity, relevantEntity);
//            if (potentialCorrespondence != null){
//                potentialCorrespondences.add(potentialCorrespondence);
//            }
//        }
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
//        for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
//            Statement stmt = i.next();
//            info += "Property: " + stmt.getPredicate().getLocalName() + "\n";
//            info += "Value: " + stmt.getObject().toString() + "\n";
//        }
        return info;
    }
}






















