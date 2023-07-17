import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Context extends Thread {

    private Agent _agent;
    //rules
    private MQ _mq;


    private AgentState state;

    public Context(Agent agent, LinkedBlockingQueue<Message> queue){
         state = new ConcreteStatus1(agent, queue);
    }
    public void setState(AgentState state){
        this.state = state;
    }
    public void processing() throws Exception {
        state.processing(this);
    }




}
