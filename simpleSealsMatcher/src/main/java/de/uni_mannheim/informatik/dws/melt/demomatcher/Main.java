package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.google.gson.GsonBuilder;
import de.uni_mannheim.informatik.dws.melt.matching_data.TestCase;
import de.uni_mannheim.informatik.dws.melt.matching_data.Track;
import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentParser;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Correspondence;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {
//        initDatabase();
//        runMatcherWithLocalData();

//        testOntClassNullURL();
//        testOntModelProperties();
//        testMatcherOnline();
//        testOboInOwl();
        calculateStaticsManually();
    }

    private static void calculateStaticsManually() throws IOException, SAXException {
        File referenceFile = new File("simpleSealsMatcher/src/main/java/DataSet/reference.rdf");
        List<List<String>> alignment = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("alignment.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("s")){
                    continue;
                }
                String[] values = line.split(",");
                alignment.add(Arrays.asList(values));
            }
        }

        Alignment reference = AlignmentParser.parse(referenceFile);

        Alignment tp = new Alignment();
        Alignment tn = new Alignment();
        Alignment fp = new Alignment();
        Alignment fn = new Alignment();
        for (List<String> myVar : alignment){
            String sourceUri = myVar.get(0).trim();
            String targetUri = myVar.get(1).trim();
            double confidence = Double.parseDouble(myVar.get(2).trim());
            for (Correspondence referenceVar : reference){
                if (referenceVar.getEntityOne().trim().equals(sourceUri)
                        && referenceVar.getEntityTwo().trim().equals(targetUri)){
                    if (referenceVar.getConfidence() == confidence){
                        tp.add(new Correspondence(sourceUri, targetUri, confidence));
                    } else if (referenceVar.getConfidence() > 0.5){
                        tn.add(new Correspondence(sourceUri, targetUri, confidence));
                    } else {
                        fp.add(new Correspondence(sourceUri, targetUri, confidence));
                    }
                    reference.remove(referenceVar);
                    break;
                }
                if (referenceVar.getEntityOne().trim().equals(targetUri)
                        && referenceVar.getEntityTwo().trim().equals(sourceUri)){
                    if (referenceVar.getConfidence() == confidence){
                        tp.add(new Correspondence(sourceUri, targetUri, confidence));
                    } else if (referenceVar.getConfidence() > 0.5){
                        tn.add(new Correspondence(sourceUri, targetUri, confidence));
                    } else {
                        fp.add(new Correspondence(sourceUri, targetUri, confidence));
                    }
                    reference.remove(referenceVar);
                    break;
                }
            }
        }

        System.out.println(reference.size());
        // those in reference but not in alignment
        for (Correspondence var : reference){
            if (var.getConfidence() > 0.5){
                tn.add(var);
            } else {
                fn.add(var);
            }
        }

        System.out.println("tp: " + tp.size() + ", tn: " + tn.size() + ", fp: " + fp.size() + ", fn: " + fn.size());
        // tp: 1010, tn: 506, fp: 0, fn: 0

        print(alignment.size() - tp.size() - tn.size() + " ");
        //338

        int falseNegative = alignment.size() - tp.size() - tn.size();

        print("precision: " + (double) tp.size() / (tp.size() + fp.size()));    // 1.0
        print("recall: " + (double) tp.size() / (tp.size() + falseNegative));   // 0.749258
        print("f1 score: " + (double) 2 * tp.size() / (2 * tp.size() + fp.size() + falseNegative)); // 0.856658

    }

    private static void testOboInOwl() {
        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        source.read("simpleSealsMatcher/src/main/java/DataSet/human.owl");
//        for (OntClass var : source.listClasses().toList()){
//            print(var.getURI());
//        }
        OntClass var = source.getOntClass("http://human.owl#NCI_C12499");
        print(OntologyAgent.toStringForGPT(var, true));
    }


    private static void initDatabase() throws IOException {
        uploadEmbeddingsFromFileToWeaviate("source.json", "Source");
        uploadEmbeddingsFromFileToWeaviate("target.json", "Target");
    }

    private static void runMatcherWithLocalData(){
        File sourceFile = new File("simpleSealsMatcher/src/main/java/DataSet/human.owl");
        File targetFile = new File("simpleSealsMatcher/src/main/java/DataSet/mouse.owl");
        File referenceFile = new File("simpleSealsMatcher/src/main/java/DataSet/reference.rdf");
        // let's execute our matcher on the OAEI Anatomy test case
        ExecutionResultSet ers = Executor.run(
                new TestCase("localtest", sourceFile.toURI(), targetFile.toURI(), referenceFile.toURI(),
                        new Track("", "", "", false) {
                            @Override
                            protected void downloadToCache() throws Exception {
                                return;
                            }
                        }), new MyMatcher());

        // let's evaluate our matcher (you can find the results in the `results` folder (will be created if it
        // does not exist).
        EvaluatorCSV evaluatorCSV = new EvaluatorCSV(ers);
        evaluatorCSV.writeToDirectory();
    }

    private static void testOntClassNullURL() {
        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        source.read("simpleSealsMatcher/src/main/java/DataSet/human.owl");

        for (OntClass var : source.listClasses().toList()) {
            if (var.getURI() == null){
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
        }
    }

    private static void uploadEmbeddingsFromFileToWeaviate(String fileName, String collectionName) throws IOException {
        print("uploading embeddings to weaviate collection " + collectionName + " ...");
        FileReader fileReader = new FileReader(fileName);
        JSONReader jsonReader = new JSONReader(fileReader);

        Weaviate db = new Weaviate();

//        ArrayList<JSONObject> rows = new ArrayList<>();
        while(fileReader.ready()){
            JSONObject var = jsonReader.readObject(JSONObject.class);
            if (var.get("uri") == null){
                continue;
            }
            ArrayList<Float> vector = new ArrayList<>();
            for (Object bigDecimal : (JSONArray) var.get("vector")) {
                vector.add(((BigDecimal) bigDecimal).floatValue());
            }
//            var.put("vector", vector);

            db.client.data().creator()
                    .withClassName(collectionName)
                    .withVector(vector.toArray(new Float[0]))
                    .withProperties(new HashMap<String, Object>() {{
                        put("uri", var.get("uri"));
                        put("isNegotiated", var.get("isNegotiated")); // will be automatically added as a number property
                    }})
                    .run();

//            rows.add(var);
        }

//        Zilliz db = new Zilliz(collectionName).initCollection();
//        db.insert(rows);
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
        source.read("simpleSealsMatcher/src/main/java/DataSet/human.owl");

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


















