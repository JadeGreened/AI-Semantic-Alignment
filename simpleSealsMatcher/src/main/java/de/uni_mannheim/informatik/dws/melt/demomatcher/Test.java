package de.uni_mannheim.informatik.dws.melt.demomatcher;

import io.metaloom.qdrant.client.http.impl.HttpErrorException;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Test {

    public static void main(String[] args) throws IOException, HttpErrorException {
        String ontology1Path = "D:\\WorkSpace\\projects\\AI-Semantic-Alignment-\\01_OntologiesTask11\\TestDataSet\\humanTest.owl";
        String ontology1 = new String(Files.readAllBytes(Paths.get(ontology1Path)), StandardCharsets.UTF_8);
        String pattern = "your_regex_pattern"; // replace with your actual regex pattern
        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m1 = r.matcher(ontology1);
        List<String> matches1 = new ArrayList<>();
        while (m1.find()) {
            matches1.add(m1.group());
        }

        String ontology2Path = "D:\\WorkSpace\\projects\\AI-Semantic-Alignment-\\01_OntologiesTask11\\TestDataSet\\mouseTest.owl";
        String ontology2 = new String(Files.readAllBytes(Paths.get(ontology2Path)), StandardCharsets.UTF_8);
        Matcher m2 = r.matcher(ontology2);
        List<String> matches2 = new ArrayList<>();
        while (m2.find()) {
            matches2.add(m2.group());
        }

        String collectionName = "embededComponentPair";
        List<List<String>> pairs = new ArrayList<>();
        for (String match1 : matches1) {
            for (String match2 : matches2) {
                pairs.add(Arrays.asList(match1,match2));
            }
        }

        Qdrant qdrant = new Qdrant(); // Assume Qdrant is a predefined class
        qdrant.initiatingDataBase(collectionName);
        qdrant.sendToDatabase(matches2, collectionName);

        Map<String, String> trueAlignDict = new HashMap<>();
        for (int i = 0; i < matches1.size(); i++) {
            trueAlignDict.put(matches1.get(i), matches2.get(i));
        }
        List<String> keys = new ArrayList<>(trueAlignDict.keySet());
        List<Pair<String, String>> trueAlignList = new ArrayList<>();
        for (Map.Entry<String, String> entry : trueAlignDict.entrySet()) {
            trueAlignList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        List<String> semanticList = qdrant.query(matches1.get(1), collectionName);
        // the aligned pairs obtained from the alignment operation
        List<Pair<String, String>> alignList = new ArrayList<>();
        ChatGPT gpt = new ChatGPT(); // Assume ChatGPT is a predefined class
        for (String match : matches1) {
            semanticList = qdrant.query(match, collectionName);
            for (String item : semanticList) {
                String components = "[" + match + ":" + item + "]";
                String result = gpt.toChatGPT(String.format(prompt7, components));
                System.out.println(result);
                if (result.equalsIgnoreCase("yes")) {
                    alignList.add(new Pair<>(match, item));
                }
            }
        }
    }
}

