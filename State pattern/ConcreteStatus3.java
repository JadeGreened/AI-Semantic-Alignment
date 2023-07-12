import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcreteStatus3 implements AgentState{
    private final Agent agent;
    final LinkedBlockingQueue<Message> queue;
    @Override
    public void processing(Context context) throws Exception {
        Message take;
        while (true) {
            take = queue.take();
            if (!take.label.equals(agent.name)) {
                System.out.println(agent.name+"--------------------change to 3----------------------");
                String promptFormat = "You are a generative artificial agent with the ability to negotiate with other generative agent. You will receive two groups of summary of the IoT ontology component in different knowledge net." +
                        "The infomation is listed below group1:{%s}, group2:{%s}. Your tasks are listed as following.When the answers is equal(equal means the same ), you will need to deal with these groups, make further explaination and" +
                        " check if the the alignment is logically correct.If so, answer \"Indeed yes\".In this scenario,the explanation is not required. When the answer is not equal ,you will need to double check the outcome and answer \"Indeed no\" and make explanation.";
                System.out.println("the agent is making the final confirmation");
                String thought = agent.think("http://127.0.0.1:8080/toChatGPT", String.format(promptFormat, agent.list.get(1), take));
                agent.list.add(thought);
                context.setState(new ConcreteStatus3(this.agent,queue));
                checkText(thought);
                break;
            }else {
                System.out.println("this is not corresponding message");
                System.out.println(1);
                queue.put(take);
            }

            System.out.println("the agent will be dead");
        }



    }
    public ConcreteStatus3(Agent agent, LinkedBlockingQueue<Message> queue){
        this.agent = agent;
        this.queue = queue;
    }

    public static void checkText(String text) {
        if (text.contains("indeed yes")) {
            System.out.println("aligned successfully");
        } else if (text.contains("indeed no")) {
            System.out.println("aligned failed");
        } else {
            System.out.println("going wrong");
        }
    }

}
