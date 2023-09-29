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

        ChatCompletions chatCompletions = client.getChatCompletions("gpt-4", new ChatCompletionsOptions(chatMessages));
        String result = chatCompletions.getChoices().get(0).getMessage().getContent();
        System.out.println(result);
        return result;
    }
}


