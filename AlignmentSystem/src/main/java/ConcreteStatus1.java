import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcreteStatus1 implements AgentState{
    private final Agent agent;
    final LinkedBlockingQueue<Message> queue;

    public void processing(Context context) throws Exception {
        System.out.println(agent.name+" is acting at first stage");
        System.out.println("now receiving data");
        String source = agent.getSource();
        System.out.println("now identifying the message");
        String promptFormat = "\"Your task is to make clarifications about the ontology I gave you. You can cite the original text to make explanations. Make the complete identification finally make it short" +
                "and sweet. The clarification should be around 30 words.Please make the definition you provide easier to judge for other artificial intelligence models. ontology:%s. The structure of the output should be like this: Explanation:<your explanation>Definition:<your definition>";
        String thought = agent.think("http://127.0.0.1:8080/toChatGPT", String.format(promptFormat,source));
        System.out.println("sending messages");
        queue.put(new Message(agent.name, thought));
        agent.list.add(thought);
        context.setState(new ConcreteStatus2(this.agent,queue));

    }
    public ConcreteStatus1(Agent agent, LinkedBlockingQueue<Message> queue){
        this.agent = agent;
        this.queue = queue;
        System.out.println(agent.name+" is activated");
    }
}
