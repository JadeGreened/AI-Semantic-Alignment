package de.uni_mannheim.informatik.dws.melt.demomatcher;
import com.google.gson.internal.LinkedTreeMap;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
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

    public static void main(String[] args) {
        Result<Meta> meta = client.misc().metaGetter().run();
        if (meta.getError() == null) {
            System.out.printf("meta.hostname: %s\n", meta.getResult().getHostname());
            System.out.printf("meta.version: %s\n", meta.getResult().getVersion());
            System.out.printf("meta.modules: %s\n", meta.getResult().getModules());
        } else {
            System.out.printf("Error: %s\n", meta.getError().getMessages());
        }
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
                .withClassName("Source")
                .withFields(uri, _additional)
                .withWhere(where)
                .run();
        if (result.hasErrors()) {
            System.out.println(result.getError());
            return null;
        }
        return (String) ((ArrayList<LinkedTreeMap>) ((LinkedTreeMap) ((LinkedTreeMap) result.getResult().getData()).get("Get")).get("Source")).get(0).get("uri");
    }
}