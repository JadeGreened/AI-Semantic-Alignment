import java.util.concurrent.BlockingQueue;

public class Message{
    public String label;
    public String msg;
    public Message(String label,String msg){
        this.label = label;
        this.msg = msg;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String get_msg() {
        return msg;
    }

    public void set_msg(String msg) {
        this.msg = msg;
    }


}

