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
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException {
        initDatabase();
        runMatcherWithLocalData();

//        testOntClassNullURL();
//        testOntModelProperties();
//        testMatcherOnline();
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


















