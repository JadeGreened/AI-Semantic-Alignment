package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentParser;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException {
//        uploadEmbeddingsFromFile("target.json", "target");


        runMatcherWithLocalData();
//        testOntModelProperties();
//        testMatcherOnline();
    }

    private static void uploadEmbeddingsFromFile(String fileName, String collectionName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        JSONReader jsonReader = new JSONReader(fileReader);

        ArrayList<JSONObject> rows = new ArrayList<>();
        while(fileReader.ready()){
            JSONObject var = jsonReader.readObject(JSONObject.class);
            if (var.get("uri") == null){
                continue;
            }
            ArrayList<Float> vector = new ArrayList<>();
            for (Object bigDecimal : (JSONArray) var.get("vector")) {
                vector.add(((BigDecimal) bigDecimal).floatValue());
            }
            var.put("vector", vector);
            rows.add(var);
        }

        Zilliz db = new Zilliz(collectionName).initCollection();
        db.insert(rows);
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
        // for shiyao
        source.read("C:\\Users\\zhang\\Documents\\Repo\\AI-Semantic-Alignment\\simpleSealsMatcher\\src\\main\\java\\DataSet\\human.owl");
        target.read("C:\\Users\\zhang\\Documents\\Repo\\AI-Semantic-Alignment\\simpleSealsMatcher\\src\\main\\java\\DataSet\\mouse.owl");
        try {
            reference = AlignmentParser.parse("C:\\Users\\zhang\\Documents\\Repo\\AI-Semantic-Alignment\\simpleSealsMatcher\\src\\main\\java\\DataSet\\reference.rdf");
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


















