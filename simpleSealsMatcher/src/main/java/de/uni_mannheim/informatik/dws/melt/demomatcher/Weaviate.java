package de.uni_mannheim.informatik.dws.melt.demomatcher;
import com.google.gson.internal.LinkedTreeMap;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.misc.model.Meta;
import io.weaviate.client.v1.schema.model.WeaviateClass;

import java.util.ArrayList;

public class Weaviate {

    public static final WeaviateClient client = new WeaviateClient(new Config("http", "localhost:8080"));
    private String collectionName;
    public Weaviate(){}
    public Weaviate(String collectionName){
        this.collectionName = collectionName;
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
        return (String) ((ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)).get(0).get("uri");
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

    public ArrayList<String> getUrisNotNegotiated(ArrayList<Double> embedding, double threshold) {
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

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(collectionName)
                .withFields(uri, _additional)
                .withNearVector(nearVector)
                .withWhere(where)
                .run();

        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        ArrayList<String> uris = new ArrayList<>();
        for (LinkedTreeMap resutl : (ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get(collectionName)) {
            LinkedTreeMap additional = (LinkedTreeMap) resutl.get("_additional");
            if ((double) additional.get("certainty") >= threshold) {
                System.out.println(resutl.get("uri"));
                uris.add((String) resutl.get("uri"));
            }
        }
        if (uris.isEmpty()){
            return null;
        }
        return uris;
    }
}