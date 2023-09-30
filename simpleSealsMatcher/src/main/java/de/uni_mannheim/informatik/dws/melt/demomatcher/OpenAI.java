package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.alibaba.fastjson.JSONArray;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.core.exception.HttpResponseException;

import java.math.BigDecimal;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

public class OpenAI {
    private static final String azureOpenaiKey = "3d172d13e1984f8ca5759838ea318a13";
    private static final String endpoint = "https://research-gpt-openai-canada-east.openai.azure.com/";

    private static final OpenAIClient client = new OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(new AzureKeyCredential(azureOpenaiKey))
            .buildClient();

    public int[] comepareComponenties(String source, String[] targets) {
        String prompt = "<Problem Definition>  \n" +
                "In this task, we are givinga) one subject ontology, " +
                "and b) a set of ontologies for potential alignment in the form of " +
                "Relation(Subject, SetOntologies), which consist of classes and properties.  \n" +
                "<Subject Ontology>  \n %s \n \n" + // subject ontology
                "%s \n \n" +    // ontology in sets
                "Among all ontologies in the set, select all ontologies that you think " +
                "having a possibility aligning with the subject ontology? Please only answer " +
                "with the index of ontology (just the index, for example \"1, 2, 4\"). Answer \"no\" " +
                "if you think none of them aligns with the subject ontology.";
        String targetsString = "";
        for (int i = 0; i < targets.length; i++) {
            targetsString += String.format("<Ontology %d in set>  \n %s \n \n", i + 1, targets[i]);
        }
        String input = String.format(prompt, source, targetsString);
        String thought = think(input);

        if (thought.contains("no")) {
            return new int[0];
        }

        String[] results = thought.split(",");
        int[] result = new int[results.length];
        for (int i = 0; i < results.length; i++) {
            result[i] = Integer.parseInt(results[i].trim()) - 1;
        }

        return result;
    }

    public boolean comepareComponenties(String component1, String component2){
        String prompt = "<Problem Definition>\n" +
                "In this task, we are given two ontologies in the form of Relation(Subject, Object), which\n" +
                "consist of classes and properties.\n" +
                "<Ontologies Triples>\n" +
                "[Ontology 1:Ontology2]:%s\n" +
                "    Do you think these two component are aligned? If so, please output:yes, otherwise, please output:no(just\"yes\" or \"no\", small character no other symbols required) ";

        String ontologies = String.format("[%s,%s]", component1, component2);
        String input = String.format(prompt, ontologies);
        String thought = think(input);

        // check if the thought is yes or no
        if (thought.toLowerCase().contains("yes")){
            return true;
        }
        return false;
    }

    /***
     * Compare between targets, decide which one is a better choice aligning to the source
     * @param source the source component
     * @param targets the target components
     * @param expertBeliefIndex the index of the target that the expert believes is the best choice. The index
     *                          must be in the range of [1, targets.length]. 0 if ignore the expert belief.
     * @return the index of the best target. The index must be in the range of [0, targets.length - 1]. -1 if something goes wrong.
     */
    public int whichComponentIsBetter(String source, String[] targets, int expertBeliefIndex){
        String thought = think(getWhichIsBetterPrompt(source, targets, expertBeliefIndex));

        // format the result into an integer
        try{
            int result = Integer.parseInt(thought);
            if (result > 0 && result <= targets.length){
                return result - 1;
            }
        } catch (NumberFormatException e){
            System.out.println("Azure: The result is not a number.");
        }
        return -1;
    }

    private String getWhichIsBetterPrompt(String source, String[] targets, int expertBeliefIndex){
        String prompt = "<Problem Definition>\n" +
                "In this task, we are giving a) one subject ontology, " +
                "and b) a set of ontologies for potential alignment in the form of " +
                "Relation(Subject, SetOntologies), which consists of classes and properties.\n \n" +
                "<Subject Ontology>  \n %s \n \n" + // subject ontology
                "%s" +  //"<Ontology 1 in set>  \n %s \n \n" + ...
                "%s" +  // expert belief
                "Among all ontologies in the set, which one do you think aligns " +
                "with the subject ontology best? Please only answer with the index of " +
                "ontology (just the index, for example \"1\", \"2\", \"3\",...)";

        String targetsString = "";
        for (int i = 0; i < targets.length; i++) {
            targetsString += String.format("<Ontology %d in set>  \n %s \n \n", i + 1, targets[i]);
        }
        String expertBeliefString = "";
        if (expertBeliefIndex > 0) {
            expertBeliefString = String.format("Some experts believe that ontology %d in set is a better alignment " +
                    "with the subject ontology among other ontologies in set. ", expertBeliefIndex);
        }

        return String.format(prompt, source, targetsString, expertBeliefString);
    }

    public List<Float> getEmbeddings(String prompt) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(Arrays.asList(prompt));
        Embeddings embeddings = client.getEmbeddings("text-embedding-ada-002", embeddingsOptions);
        List<Double> var =  embeddings.getData().get(0).getEmbedding();

        ArrayList<Float> vector = new ArrayList<>();
        for (Double value : var) {
            vector.add(value.floatValue());
        }
        return vector;
    }

    private String think(String prompt) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.USER, prompt));
        ChatCompletions chatCompletions = null;
        boolean flag = false;
        while(!flag){
            try{
                chatCompletions = client.getChatCompletions("gpt-4", new ChatCompletionsOptions(chatMessages));
                flag = true;
            } catch (HttpResponseException e){
//                System.out.println(e.getMessage());
                System.out.println("Azure: Waiting for the server to be ready...");
            }
        }
        String result = chatCompletions.getChoices().get(0).getMessage().getContent();
//        System.out.println(result);
        return result;
    }
}


