package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.matching_data.TestCase;
import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentParser;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentSerializer;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.xml.sax.SAXException;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        runMatcherWithLocalData();
//        testOntModelProperties();
//        testMatcherOnline();
    }

    /***
     * Run the matcher with local resource.
     */
    private static void runMatcherWithLocalData() {

        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        OntModel target = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        Alignment reference;
        // for windows
//        source.read("D:\\WorkSpace\\projects\\sealsproj\\simpleSealsMatcher\\src\\main\\java\\DataSet\\human.owl");
//        target.read("D:\\WorkSpace\\projects\\sealsproj\\simpleSealsMatcher\\src\\main\\java\\DataSet\\mouse.owl");
        // for mac
        source.read("/Users/shiyaozhang/Developer/AI-Semantic-Alignment/simpleSealsMatcher/src/main/java/DataSet/human.owl");
        target.read("/Users/shiyaozhang/Developer/AI-Semantic-Alignment/simpleSealsMatcher/src/main/java/DataSet/mouse.owl");
        try {
            reference = AlignmentParser.parse("/Users/shiyaozhang/Developer/AI-Semantic-Alignment/simpleSealsMatcher/src/main/java/DataSet/reference.rdf");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MyMatcher myMatcher = new MyMatcher();
        Alignment alignment;
        try {
            alignment = myMatcher.match(source, target, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Test the matcher with online resource to have statistics.
     */
    private static void testMatcherOnline(){
        // let's execute our matcher on the OAEI Anatomy test case
        ExecutionResultSet ers = Executor.run(TrackRepository.Anatomy.Default.getFirstTestCase(), new MyMatcher());

        // let's evaluate our matcher (you can find the results in the `results` folder (will be created if it
        // does not exist).
        EvaluatorCSV evaluatorCSV = new EvaluatorCSV(ers);
        evaluatorCSV.writeToDirectory();
    }

    private static void testOntModelProperties(){
        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        source.read("/Users/shiyaozhang/Developer/AI-Semantic-Alignment/simpleSealsMatcher/src/main/java/DataSet/human.owl");
        OntClass var = source.listClasses().next();
        print("var==================");
        print(var.getLabel(null));
        print(var.getURI());
        print(var.getLocalName());
        print(var.getComment(null));
        print(var.getNameSpace());
        print(var.getVersionInfo());
        if (var.getEquivalentClass() != null){
            print("equivalent class ================");
            print(var.getEquivalentClass().getURI());
            print(var.getEquivalentClass().getLocalName());
            print(var.getEquivalentClass().getLabel(null));
            print(var.getEquivalentClass().getComment(null));
        }
        if(var.getSubClass() != null){
            print("subclass========================");
            print(var.getSubClass().getURI());
            print(var.getSubClass().getLocalName());
            print(var.getSubClass().getLabel(null));
            print(var.getSubClass().getComment(null));
        }
        if(var.getSuperClass() != null){
            print("superclass========================");
            print(var.getSuperClass().getURI());
            print(var.getSuperClass().getLocalName());
            print(var.getSuperClass().getLabel(null));
            print(var.getSuperClass().getComment(null));
        }

        print(var.getSameAs() == null ? "null" : var.getSameAs().toString());
        print(var.getDisjointWith() == null ? "null" : var.getDisjointWith().toString());
    }

    private static void print(String s){
        System.out.println(s);
    }
}


















