package de.uni_mannheim.informatik.dws.melt.demomatcher;
import com.google.gson.internal.LinkedTreeMap;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.misc.model.Meta;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @See https://weaviate.io/developers/weaviate/api/graphql for more information.
 */
public class Weaviate {

    public static final WeaviateClient client = new WeaviateClient(new Config("http", "localhost:8080"));
    private String collectionName;
    public Weaviate(){}
    public Weaviate(String collectionName){
        this.collectionName = collectionName;
    }

    public static void main(String[] args){
        findHowManyEntryThereAreInTheDatabaseForEachClass("Source");
        findHowManyEntryThereAreInTheDatabaseForEachClass("Target");
    }

    private static void findHowManyEntryThereAreInTheDatabaseForEachClass(String testCollectionName) {
        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        source.read("simpleSealsMatcher/src/main/java/DataSet/human.owl");
        int count = 0;
        for (OntClass var : source.listClasses().toList()){
            Field uriField = Field.builder().name("uri").build();
            Field netotiatedField = Field.builder().name("isNegotiated").build();
            Field _additional = Field.builder()
                    .name("_additional")
                    .fields(new Field[]{
                            Field.builder().name("vector").build(),
                            Field.builder().name("id").build(),
                    }).build();
            WhereFilter where = WhereFilter.builder()
                    .path(new String[]{ "uri" })
                    .operator(Operator.Equal)
                    .valueString(var.getURI())
                    .build();

            Result<GraphQLResponse> result = client.graphQL().get()
                    .withClassName(testCollectionName)
                    .withFields(uriField, netotiatedField, _additional)
                    .withWhere(where)
                    .run();
            if (result.hasErrors()) {
                continue;
            }
            List<Map> list = (List<Map>)((Map) ((Map) result.getResult().getData()).get("Get")).get("Source");
            if (list == null){
                continue;
            }
            count++;
            if (list.size() != 1){
                System.out.println(list.get(0).get("uri") + " " + count);
            }
        }
        System.out.println(count);
        return;
    }

    public String getUriForNotNegotiated(){
        Field uri = Field.builder().name("uri").build();
        Field _additional = Field.builder()
                .name("_additional")
                .fields(new Field[]{
                        Field.builder().name("vector").build(),
                }).build();
        WhereFilter where = WhereFilter.builder()
                .path(new String[]{ "isNegotiated" })
                .operator(Operator.Equal)
                .valueBoolean(false)
                .build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(collectionName)
                .withFields(uri, _additional)
                .withWhere(where)
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        try{
            return (String) ((ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)).get(0).get("uri");
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public ArrayList<Double> getEmbedding(String uri){
        Field uriField = Field.builder().name("uri").build();
        Field _additional = Field.builder()
                .name("_additional")
                .fields(new Field[]{
                        Field.builder().name("vector").build(),
                }).build();
        WhereFilter where = WhereFilter.builder()
                .path(new String[]{ "uri" })
                .operator(Operator.Equal)
                .valueString(uri)
                .build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(collectionName)
                .withFields(uriField, _additional)
                .withWhere(where)
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        return (ArrayList<Double>) ((LinkedTreeMap) ((ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)).get(0).get("_additional")).get("vector");
    }

    public ArrayList<String> getUrisNotNegotiated(ArrayList<Double> embedding, double threshold, boolean all) {
        Float[] embeddingFloat = new Float[1536];
        for (int i = 0; i < embedding.size(); i++) {
            embeddingFloat[i] = embedding.get(i).floatValue();
        }

        Field uri = Field.builder().name("uri").build();
        Field _additional = Field.builder()
                .name("_additional")
                .fields(new Field[]{
                        Field.builder().name("certainty").build(),  // only supported if distance==cosine
                        Field.builder().name("distance").build()   // always supported
                }).build();
        WhereFilter where = WhereFilter.builder()
                .path(new String[]{ "isNegotiated" })
                .operator(Operator.Equal)
                .valueBoolean(false)
                .build();
        NearVectorArgument nearVector = NearVectorArgument.builder()
                .vector(embeddingFloat)
                .build();

        Result<GraphQLResponse> result = null;
        if (all){
            result = client.graphQL().get()
                    .withClassName(collectionName)
                    .withFields(uri, _additional)
                    .withNearVector(nearVector)
                    .run();
        } else {
            result = client.graphQL().get()
                    .withClassName(collectionName)
                    .withFields(uri, _additional)
                    .withNearVector(nearVector)
                    .withWhere(where)
                    .run();
        }

        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        ArrayList<String> uris = new ArrayList<>();
        ArrayList<LinkedTreeMap> results = (ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName);
        if (results == null || results.isEmpty()){
            return null;
        }
        for (LinkedTreeMap resutl : (ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)) {
            LinkedTreeMap additional = (LinkedTreeMap) resutl.get("_additional");
            if ((double) additional.get("certainty") >= threshold) {
//                System.out.println(resutl.get("uri"));
                uris.add((String) resutl.get("uri"));
            }
        }
        if (uris.isEmpty()){
            return null;
        }
        return uris;
    }

    public void markNegotiated(String sourceUri) {
        boolean result = updateNegotiated(sourceUri, true);
        if (!result){
            System.out.println("Error: Failed to mark negotiated.");
        }
    }

    private boolean updateNegotiated(String uri, boolean isNegotiated){
        String id = getId(uri);
        if (id == null){
            return false;
        }
        Result<Boolean> result = client.data().updater()
                .withMerge()
                .withID(id)
                .withClassName(collectionName)
                .withProperties(new HashMap<String, Object>(){{
                    put("isNegotiated", isNegotiated);
                }})
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
        }
        return result.getResult();
    }

    private String getId(String uri){
        Result<GraphQLResponse> result = get(uri);
        return (String) ((LinkedTreeMap) ((ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)).get(0).get("_additional")).get("id");
    }

    private Result<GraphQLResponse> get(String uri){
        Field _additional = Field.builder()
                .name("_additional")
                .fields(new Field[]{
                        Field.builder().name("id").build(),
                        Field.builder().name("vector").build(),
                }).build();
        WhereFilter where = WhereFilter.builder()
                .path(new String[]{ "uri" })
                .operator(Operator.Equal)
                .valueString(uri)
                .build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(collectionName)
                .withFields(_additional)
                .withWhere(where)
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        return result;
    }
}