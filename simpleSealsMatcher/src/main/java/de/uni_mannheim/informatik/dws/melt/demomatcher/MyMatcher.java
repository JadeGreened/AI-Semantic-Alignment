package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Correspondence;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class MyMatcher extends MatcherYAAAJena {
    private OntologyAgent sourceAgent;
    private OntologyAgent targetAgent;
    @Override
    public Alignment match(OntModel source, OntModel target, Alignment inputAlignment, Properties properties) throws Exception {
        return matchLogic(source, target, false);
    }

    public Alignment matchLocally(OntModel source, OntModel target){
        return matchLogic(source, target, false);
    }

    private Alignment matchLogic(OntModel source, OntModel target, boolean isOnline){
        print("Alignment begin.");
        // setup agents, embeddings, database, and openAI
        setup(source, target, isOnline);

        Alignment alignment = new Alignment();
        int alignmentCount = 0;
        int negotiationRound = 0;

        // start alignment
        // if there is at least one agent has unaligned components
        while (!sourceAgent.isFinished() || !targetAgent.isFinished()){
            // if source agent has unaligned components
            if (!sourceAgent.isFinished()) {
                Correspondence correspondence = startNegotiationForOneEntity(sourceAgent, targetAgent);
                if (correspondence != null){
                    alignment.add(correspondence);
                    print("Current alignment count: " + ++alignmentCount + ". Max pair count: 3304. Reference count: 1516.");
                }
                print("Current negotiation round: " + ++negotiationRound + ". Max round: 6048 = 3304 + 2744. Reference round: 1516.");
            }
            // if target agent has unaligned components
            if (!targetAgent.isFinished()) {
                Correspondence correspondence = startNegotiationForOneEntity(targetAgent, sourceAgent);
                if (correspondence != null){
                    alignment.add(correspondence);
                    print("Current alignment count: " + ++alignmentCount + ". Max pair count: 3304. Reference count: 1516.");
                }
                print("Current negotiation round: " + ++negotiationRound + ". Max round: 6048 = 3304 + 2744. Reference round: 1516.");
            }
        }

        // TODO: resolve attack graph
//        Alignment toRemove = removeAttack(alignment, source, target);
//        alignment.removeAll(toRemove);

        // clean database
        clean();

        return alignment;
    }

    public Alignment removeAttack(Alignment alignment, OntModel source, OntModel target){
        Alignment toRemove = new Alignment();
        int count = 0;
        for (Correspondence var1 : alignment){
            for (Correspondence var2: alignment){
                if (var1.equals(var2)){
                    continue;
                }
                boolean flag = false;
                if (var1.getEntityOne().equals(var2.getEntityOne())) {
                    flag = true;
                }
                if (var1.getEntityOne().equals(var2.getEntityTwo())) {
                    flag = true;
                }
                if (var1.getEntityTwo().equals(var2.getEntityOne())) {
                    flag = true;
                }
                if (var1.getEntityTwo().equals(var2.getEntityTwo())) {
                    flag = true;
                }
                if (flag){
                    // TODO: ask GPT which to keep
                    OntClass source1 = null;
                    OntClass target1 = null;
                    OntClass source2 = null;
                    OntClass target2 = null;
                    try {
                        source1 = source.getOntClass(var1.getEntityOne());
                        target1 = target.getOntClass(var1.getEntityTwo());
                    } catch (Exception e){
                        source1 = target.getOntClass(var1.getEntityOne());
                        target1 = source.getOntClass(var1.getEntityTwo());
                    }
                    try {
                        source2 = source.getOntClass(var2.getEntityOne());
                        target2 = target.getOntClass(var2.getEntityTwo());
                    } catch (Exception e){
                        source2 = target.getOntClass(var2.getEntityOne());
                        target2 = source.getOntClass(var2.getEntityTwo());
                    }
                    Correspondence var  = sourceAgent.resolveAttack(source1, target1, source2, target2);
                    toRemove.add(var);
                    count++;
                }
            }
        }
        print("Remove attack count: " + count);
        return toRemove;
    }

    /***
     * setup agents, embeddings, database, and openAI
     * @param source source ontology
     * @param target target ontology
     */
    public void setup(OntModel source, OntModel target, boolean isOnline){
        this.targetAgent = new OntologyAgent(target, "Target", isOnline);
        this.sourceAgent = new OntologyAgent(source, "Source", isOnline);
    }

    /***
     * Start negotiation.
     * Source agent pick one unaligned entity, target agent find potential alignment.
     * If there are potential alignments, source and target agent discuss which one is the best in turn,
     * with proposing new suitable components.
     * @param source source agent
     * @param target target agent
     * @return The agreed correspondence.
     */
    private Correspondence startNegotiationForOneEntity(OntologyAgent source, OntologyAgent target) {
        print("Start one Negotiation ===================================");
        // source agent pick one unaligned entity
        OntClass entity = source.startNegotiation();
        if (entity == null){
            source.Finish();
            print(source.getCollectionName() + " has no unaligned entity.");
            return null;
        }
        print(source.getCollectionName() + " pick one unaligned entity: " + entity.getLabel(null));

        ArrayList<Double> embedding = source.getEmbedding(entity);

        // target agent find potential alignment
        Set<PotentialCorrespondence> proposedCorrespondences = target.proposeCorrespondence(entity, embedding);
        if (proposedCorrespondences == null){
            print(target.getCollectionName() + " find no potential alignment for " + entity.getLabel(null));
            // mark the entity as negotiated.
            source.markNegotiated(entity);
            return null;
        }

        // target agent ask openAI which one is better.
        PotentialCorrespondence betterCorrespondence = target.whichTargetIsBetter(entity, proposedCorrespondences, null);
        if (betterCorrespondence == null){
            source.markNegotiated(entity);
            print(target.getCollectionName() + " thinks no option is good for " + entity.getLabel(null) + ". It looked at ." + proposedCorrespondences.toString());
            return null;
        }
        print(target.getCollectionName() + " find the better one: " + betterCorrespondence.getTarget().getLabel(null));

        // source agent check proposed correspondences
        PotentialCorrespondence agreement = source.whichTargetIsBetter(entity, proposedCorrespondences, betterCorrespondence.getTarget());
//        PotentialCorrespondence agreement = source.checkProposal(entity, proposedCorrespondences, betterCorrespondence, target);
        if (agreement == null){
            print(source.getCollectionName() + " dont' think any entity proposed by " + target.getCollectionName() + " is good for " + entity.getLabel(null) + ". It looked at ." + betterCorrespondence.getTarget().getLabel(null));
            source.markNegotiated(entity);
            return null;
        }
        print(source.getCollectionName() + " make agreement: " + agreement.getTarget().getLabel(null));

        source.markNegotiated(agreement.getSource());
        target.markNegotiated(agreement.getTarget());
        print("Alignment found: " + agreement.getSource().getLabel(null) + " - " + agreement.getTarget().getLabel(null));
        return new Correspondence(agreement.getSource().getURI(), agreement.getTarget().getURI(), 1, agreement.getRelation());
    }


    private void clean(){
        this.sourceAgent.clean();
        this.targetAgent.clean();
    }

    private String getURI(String data){
        // 找到 "Class  URI: " 和 "Label: " 的位置
        int startIndex = data.indexOf("Class  URI: ") + "Class  URI: ".length();
        int endIndex = data.indexOf("Label: ");

        // 使用 substring 提取 URI
        String classUri = data.substring(startIndex, endIndex).trim();

        // 将字符串转为 URI
        return classUri;
    }

    /*
    这里是用的jena的api接口来处理的owl文件，这样操作比较方便，而且能直接拿到class的URI（Uniform Resource Identifier）。并且也能找到一个ontology的全部属性和信息，
    如果你觉得不好的话可以改。
     */
    private ArrayList<String> toArrayList(OntModel ontology) {
        print("Start transfer ontology to ArrayList");

        ArrayList<String> list = new ArrayList<>();
        for (OntClass ontClass : ontology.listClasses().toList()) {
            //如果是一个匿名类，这里要拿来用的话是非常困难的，一般对齐当中也不会考虑匿名类
            //当然匿名类里面也会有很多的信息，可以拿来使用。这个就要看后续的算法优化了
            if (ontClass.isAnon()) {
                // currently just ignored, so comment out
////                System.out.println("This is a anonymous class--------------------------------");
////                System.out.println("Uri : " + ontClass.asClass().getURI());
//                for (Iterator<OntClass> i = ontClass.listSubClasses(); i.hasNext(); ) {
//                    OntClass subClass = i.next();
////                    System.out.println("anon subclass: " + subClass.getURI());
//                    //子类
//                }
//                for (Iterator<OntClass> i = ontClass.listSuperClasses(); i.hasNext(); ) {
//                    OntClass superClass = i.next();
////                    System.out.println("anon superclass: " + superClass.getURI());
//                    //超类
//                }
//                for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
//                    Statement stmt = i.next();
////                    System.out.println("Property: " + stmt.getPredicate().getLocalName());
////                    System.out.println("Value: " + stmt.getObject().toString());
//                    //属性
//                }
            } else {
                //如果是一个具名类
//                System.out.println("-----------------------------------------");
                String info = "";
                String uri = ontClass.getURI();
//                System.out.println("Class  URI: " + uri);
                info += "Class  URI: " + uri + "\n";

                // 获取并打印类的标签
                String label = ontClass.getLabel(null);
//                System.out.println("Label: " + label);
                info += "Label: " + label + "\n";
                //所有的属性
                for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
                    Statement stmt = i.next();
//                    System.out.println("Property: " + stmt.getPredicate().getLocalName());
//                    System.out.println("Value: " + stmt.getObject().toString());
                    info += "Property: " + stmt.getPredicate().getLocalName() + "\n";
                    info += "Value: " + stmt.getObject().toString() + "\n";
                }
                list.add(info);
            }
        }
        return list;
    }

    private static void print(String s){
        System.out.println(s);
    }
}
