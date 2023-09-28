package de.uni_mannheim.informatik.dws.melt.demomatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.highlevel.dml.InsertRowsParam;
import io.milvus.param.highlevel.dml.SearchSimpleParam;
import io.milvus.param.highlevel.dml.response.InsertResponse;
import io.milvus.param.highlevel.dml.response.SearchResponse;
import io.milvus.response.QueryResultsWrapper;

import io.milvus.param.collection.DropCollectionParam;

public class Zilliz {
    /*
    这是一个免费的向量数据库，里面支持很多的语言编程，这里面因为涉及到json文件，所以有很多错误，建议在运行前好好的看看这个部分的数据传输问题
    参考文档地址：https://docs.zilliz.com/docs/quick-start
     */
    private static final ConnectParam connectParam = ConnectParam.newBuilder()
            .withUri("https://in03-350996688b99398.api.gcp-us-west1.zillizcloud.com")
            // - For a serverless cluster, use an API key as the token.
            // - For a dedicated cluster, use the cluster credentials as the token
            // in the format of 'user:password'.
            .withToken("0a6b71a6a363b8d87abd92436be75fc2ea8b89912d121a3d4dbc0b43bc6fee86618697b9c785761dbecbe44e9f8d206b74378e94")
            .build();
    public static final  MilvusServiceClient client = new MilvusServiceClient(connectParam);
    private String collectionName;
    public Zilliz(String collectionName){
        this.collectionName = collectionName;
    }
    public Zilliz initCollection(){
        // 2. Create collection
        FieldType id = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType title_vector = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(1536)
                .build();
        FieldType uri = FieldType.newBuilder()
                .withName("uri")
                .withDataType(DataType.VarChar)
                .withMaxLength(512)
                .build();
        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Schema of source ontology")
                .addFieldType(id)
                .addFieldType(title_vector)
                .addFieldType(uri)
                .build();

        R<RpcStatus> collection = client.createCollection(createCollectionParam);

        if (collection.getException() != null) {
            System.out.println("Failed to create collection: " + collection.getException().getMessage());
            return null;
        }
        System.out.println("Collection " + collectionName + " created!");
        return this;
    }

    /***
     * Float vector field's value type must be List<Float>
     * @param rows
     */
    public void insert(List<JSONObject> rows){
        InsertRowsParam insertRowsParam = InsertRowsParam.newBuilder()
                .withCollectionName(collectionName)
                .withRows(rows)
                .build();

        R<InsertResponse> res = client.insert(insertRowsParam);

        if (res.getException() != null) {
            System.out.println("Failed to insert: " + res.getException().getMessage());
            return;
        }

        System.out.println("Successfully inserted " + res.getData().getInsertCount() + " records");
    }

    public String insertData(ArrayList<String> data) throws Exception {
        OpenAI openAI = new OpenAI();
        for (int i = 0; i < data.size(); i++) {
            String json = "{\n" +
                    "    \"id\": \"%s\",\n" +
                    "    \"vector\": [%s],\n" +
                    "    \"uri\": \"%s\"\n" +
                    "}";
            String Json = String.format(json, i, openAI.getEmbeddings(data.get(i)), data.get(i));
            JSONObject dataset = JSON.parseObject(Json);
            List<JSONObject> rows = getRows(dataset.getJSONArray("rows"), data.size());
            InsertRowsParam insertRowsParam = InsertRowsParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withRows(rows)
                    .build();
            R<InsertResponse> res = client.insert(insertRowsParam);

            if (res.getException() != null) {
                System.out.println("Failed to insert: " + res.getException().getMessage());
                return "failed";
            }
        }
        return "success";
    }
    public List<String> query(String ontology){
        List<String> result = new ArrayList<>();
        OpenAI openAI = new OpenAI();
        List<Float> embedding = openAI.getEmbeddings(ontology);
        // Change the second argument of the `getRows` function
        // to limit the number of rows obtained from the dataset.

        // 5. search
        List<List<Float>> queryVectors = new ArrayList<>();
        queryVectors.add(embedding);

        List<String> outputFields = new ArrayList<>();
        outputFields.add("uri");
        SearchSimpleParam searchSimpleParam = SearchSimpleParam.newBuilder()
                .withCollectionName(collectionName)
                .withVectors(queryVectors)
                .withOutputFields(outputFields)
                .withOffset(0L)
                .withLimit(5L)
                .build();

        R<SearchResponse> searchRes = client.search(searchSimpleParam);

        if (searchRes.getException() != null) {
            System.out.println("Failed to search: " + searchRes.getException().getMessage());
            return null;
        }

        for (QueryResultsWrapper.RowRecord rowRecord: searchRes.getData().getRowRecords()) {
            result.add(rowRecord.get("uri").toString());
        }
        return result;
    }

    public List<JSONObject> getRows(JSONArray dataset, int counts) {
        List<JSONObject> rows = new ArrayList<JSONObject>();
        for (int i = 0; i < counts; i++) {
            JSONObject json_row = new JSONObject(1, true);
            JSONObject original_row = dataset.getJSONObject(i);

            int id = original_row.getIntValue("id");
            List<Float> vectors = original_row.getJSONArray("vector").toJavaList(Float.class);
            String uri = original_row.getString("uri");
            json_row.put("id", id);
            json_row.put("vector", vectors);
            json_row.put("uri", uri);
            rows.add(json_row);
        }
        return rows;
    }

    public void dropCollection(){
        // TODO: store the data in a file


        DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> dropCollection = client.dropCollection(dropCollectionParam);

        if (dropCollection.getException() != null) {
            System.out.println("Failed to drop collection: " + dropCollection.getException().getMessage());
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        new Zilliz("source").dropCollection();
        new Zilliz("target").dropCollection();
    }
}