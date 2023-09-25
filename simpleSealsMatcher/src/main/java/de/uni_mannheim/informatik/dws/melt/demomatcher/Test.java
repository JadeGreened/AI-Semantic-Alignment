package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherPipelineYAAAJena;
import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.elementlevel.ExactStringMatcher;
import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.external.matcher.BackgroundMatcher;
import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.external.matcher.ImplementedBackgroundMatchingStrategies;
import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.external.wordNet.WordNetKnowledgeSource;
import io.metaloom.qdrant.client.http.impl.HttpErrorException;

import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;

import java.util.List;
import java.util.Properties;

import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.external.matcher.SimpleStringMatcher;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Test {


    //            String ontology1 = "";
//            String ontology2 = "";
//            ArrayList<String> matches1 = new ArrayList<>();
//            ArrayList<String> matches2 = new ArrayList<>();
//            String pattern = "你的正则表达式";  // 请替换为你的正则表达式
//            Pattern r = Pattern.compile(pattern);
//
//            try {
//                ontology1 = new String(Files.readAllBytes(Paths.get("D:\\WorkSpace\\projects\\AI-Semantic-Alignment-\\01_OntologiesTask11\\TestDataSet\\humanTest.owl")), "UTF-8");
//                Matcher m1 = r.matcher(ontology1);
//                while (m1.find()) {
//                    matches1.add(m1.group());
//                }
//                ontology2 = new String(Files.readAllBytes(Paths.get("D:\\WorkSpace\\projects\\AI-Semantic-Alignment-\\01_OntologiesTask11\\TestDataSet\\mouseTest.owl")), "UTF-8");
//                Matcher m2 = r.matcher(ontology2);
//                while (m2.find()) {
//                    matches2.add(m2.group());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String collection_name = "embededComponentPair";
//
//            ArrayList<ArrayList<String>> pairs = new ArrayList<>();
//            for (String match1 : matches1) {
//                for (String match2 : matches2) {
//                    pairs.add(new ArrayList<>(Arrays.asList(match1, match2)));
//                }
//            }
//
//            //这里的qdrant并没有实现，主要是我看qdrant并没有开放余弦相似度的java接口。
//            Qdrant qdrant = new Qdrant();
//            qdrant.initiatingDataBase(collection_name);
//            qdrant.sendToDatabase(matches2, collection_name);
//
//            HashMap<String, String> true_align_dict = new HashMap<>();
//            for (int i = 0; i < matches1.size(); i++) {
//                true_align_dict.put(matches1.get(i), matches2.get(i));
//            }
//
//            ArrayList<String> keys = new ArrayList<>(true_align_dict.keySet());
//            ArrayList<Map.Entry<String, String>> true_align_list = new ArrayList<>(true_align_dict.entrySet());
//
//            ArrayList<String> semantic_list = qdrant.query(matches1.get(1), collection_name);
//            ArrayList<ArrayList<String>> align_list = new ArrayList<>();
//
//            ChatGPT GPT = new ChatGPT();
//            for (String match : matches1) {
//                semantic_list = qdrant.query(match, collection_name);
//                for (String item : semantic_list) {
//                    String components = "[" + match + ":" + item + "]";
//                    String result = GPT.toChatGPT(String.format("prompt7 %s", components));  // 请替换 "prompt7" 为你的实际字符串
//                    System.out.println(result);
//                    if (result.equalsIgnoreCase("yes")) {
//                        align_list.add(new ArrayList<>(Arrays.asList(match, item)));
//                    }
//                }
//
//            }
    public static class MyPipelineMatcher extends MatcherPipelineYAAAJena {


        @Override
        protected List<MatcherYAAAJena> initializeMatchers() {
            List<MatcherYAAAJena> result = new ArrayList<>();

            // let's add a simple exact string matcher
            result.add(new ExactStringMatcher());

            // let's add a background matcher
            result.add(new BackgroundMatcher(new WordNetKnowledgeSource(),
                    ImplementedBackgroundMatchingStrategies.SYNONYMY, 0.5));

            return result;
        }


    }

    public static void main(String[] args) {
        // let's initialize our matcher
        MyPipelineMatcher myMatcher = new MyPipelineMatcher();

        // let's execute our matcher on the OAEI Anatomy test case
        ExecutionResultSet ers = Executor.run(TrackRepository.Anatomy.Default.getFirstTestCase(), myMatcher);

        // let's evaluate our matcher (you can find the results in the `results` folder (will be created if it
        // does not exist).
        EvaluatorCSV evaluatorCSV = new EvaluatorCSV(ers);
        evaluatorCSV.writeToDirectory();

    }


}


