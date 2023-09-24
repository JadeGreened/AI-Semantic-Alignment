package de.uni_mannheim.informatik.dws.melt.demomatcher;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.AzureKeyCredential;

import java.util.ArrayList;
import java.util.List;

public class ChatGPT {

    public String think(String prompt) {
        String azureOpenaiKey = "3d172d13e1984f8ca5759838ea318a13";
        String endpoint = "https://research-gpt-openai-canada-east.openai.azure.com/";
        String deploymentOrModelId = "gpt-4-32k";

        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
//        chatMessages.add(new ChatMessage(ChatRole.SYSTEM, "You are a helpful assistant"));
//        chatMessages.add(new ChatMessage(ChatRole.USER, "Does Azure OpenAI support customer managed keys?"));
//        chatMessages.add(new ChatMessage(ChatRole.ASSISTANT, "Yes, customer managed keys are supported by Azure OpenAI?"));
//        chatMessages.add(new ChatMessage(ChatRole.USER, "Do other Azure AI services support this too?"));
        chatMessages.add(new ChatMessage(ChatRole.USER,prompt));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String result = chatCompletions.getChoices().get(0).getMessage().getContent();
        System.out.println(result);
        return result;


    }
}