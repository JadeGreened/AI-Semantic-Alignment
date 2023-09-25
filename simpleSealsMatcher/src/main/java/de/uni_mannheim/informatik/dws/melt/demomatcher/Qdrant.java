//package de.uni_mannheim.informatik.dws.melt.demomatcher;
//
//import com.fasterxml.jackson.core.JacksonException;
//import io.metaloom.qdrant.client.http.QDrantHttpClient;
//import io.metaloom.qdrant.client.http.impl.HttpErrorException;
//import io.metaloom.qdrant.client.http.model.collection.CollectionCreateRequest;
//import io.metaloom.qdrant.client.http.model.collection.config.Distance;
//import io.metaloom.qdrant.client.http.model.point.PointCountRequest;
//import io.metaloom.qdrant.client.http.model.point.PointStruct;
//import io.metaloom.qdrant.client.http.model.point.PointsListUpsertRequest;
//
//import java.net.URL;
//import java.util.List;
//
//public class Qdrant {
//    private final String host = "https://70eb8777-8707-4d6a-8c86-ef987a787109.us-east-1-0.aws.cloud.qdrant.io";
//    private final int port = 6333;
//    private final QDrantHttpClient client = QDrantHttpClient.builder()
//            .setHostname(host)
//            .setPort(port)
//            .build();
//
//    public void initiatingDataBase(String collectionName) throws HttpErrorException {
//        CollectionCreateRequest req = new CollectionCreateRequest();
//        req.setVectors("colors", 4, Distance.EUCLID);
//        client.createCollection("the-collection-name", req).sync();
//    }
//    public void sendToDatabase(List<String> matches,String collectionName){
//        for (int i = 0; i < matches.size(); i++) {
//            PointStruct p = PointStruct.of();
//        }
//
//
//
//    }
//    public static void main(String[] args) {
//        int port = 6333;
//        try (QDrantHttpClient client = QDrantHttpClient.builder()
//                .setHostname(host)
//                .setPort(port)
//                .build()) {
//
//            // Create a collection
//
//
//            // Now add some points
//            PointStruct p1 = PointStruct.of("colors", 0.42f, 0.33f, 42.15f, 68.72f)
//                    .setPayload("{\"name\": \"first\"}")
//                    .setId(1);
//            PointStruct p2 = PointStruct.of("colors", 0.76f, 0.43f, 63.45f, 22.10f)
//                    .setPayload("{ \"color\": \"red\"}")
//                    .setId(2);
//            PointStruct p3 = PointStruct.of("colors", 0.41f, 0.32f, 42.11f, 68.71f).setId(3);
//            PointStruct p4 = PointStruct.of("colors", 0.12f, 0.23f, 12.46f, 47.17f).setId(4);
//
//            PointsListUpsertRequest pointsRequest = new PointsListUpsertRequest();
//            pointsRequest.setPoints(p1, p2, p3, p4);
//            client.upsertPoints("the-collection-name", pointsRequest, false).async().blockingGet();
//
//            // List the collections
//            client.listCollections().async().blockingGet();
//
//            // Count the points in the collection
//            client.countPoints("the-collection-name", new PointCountRequest().setExact(true)).sync();
//        } catch (JacksonException e) {
//            throw new RuntimeException(e);
//        } catch (HttpErrorException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
