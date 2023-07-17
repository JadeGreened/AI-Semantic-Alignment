import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Agent extends Thread {
    public String name = "";
    public String source = "";
    public String thoughts = "";
    public String status = "";
    public String output = "";
    public int threadNumber = 0;
    private final String url1 = "http://127.0.0.1:8080/toChatGPT";
    private final String url2 = "http://127.0.0.1:8080/query";
    private final int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    public ArrayList<String> list = new ArrayList();

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public Agent() {

    }
    public Agent(String name,String source){
        this.name = name;
        this.source = source;
    }

    public Agent(String name, BlockingQueue<String> queue, BlockingQueue<String> responseQueue) {
        this.name = name;

    }
    public Agent(String name, BlockingQueue<String> queue, BlockingQueue<String> responseQueue,String source){
        this.name = name;

        this.source = source;
    }

    public Agent(int threadNumber) {
        this.threadNumber = threadNumber;

    }

    public Agent(String name, String source, String thoughts, String status, String output) {
        this.name = name;
        this.source = source;
        this.thoughts = thoughts;
        this.status = status;
        this.output = output;

    }
    //...


    /**
     * 获取
     *
     * @return name
     */



    /**
     * 设置
     * @param name
     */

    /**
     * 获取
     *
     * @return source
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置
     *
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取
     *
     * @return thoughts
     */
    public String getThoughts() {
        return thoughts;
    }

    /**
     * 设置
     *
     * @param thoughts
     */
    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    /**
     * 获取
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取
     *
     * @return output
     */
    public String getOutput() {
        return output;
    }

    /**
     * 设置
     *
     * @param output
     */
    public void setOutput(String output) {
        this.output = output;
    }

    public String toString() {
        return "Agent{name = " + name + ", source = " + source + ", thoughts = " + thoughts + ", status = " + status + ", output = " + output + "}";
    }

    public String retrievalMessagaes(Object o) {
        this.source = o.getClass().toString();
        return "";
    }

    public String think(String url, String Input) throws Exception {

        URL Url = new URL(url);
        HttpURLConnection con = (HttpURLConnection) Url.openConnection();

        // Set the request method
        con.setRequestMethod("POST");

        // Enable input/output streams
        con.setDoOutput(true);

        // Write the request body
        try (OutputStream out = con.getOutputStream()) {
            out.write(Input.getBytes());
            out.flush();
        }

        // Get the response code
        int status = con.getResponseCode();
        System.out.println("-----------------------------------------------------------------------");
        String result = "";
        // Read the response
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            result = content.toString();
            System.out.println("Response Content: " + result);
        }catch (Exception e){
            e.printStackTrace();
        }
        con.disconnect();
        return result;
    }

    public void push(BlockingQueue<Message> queue,String output) throws InterruptedException {
        queue.put(new Message(this.name,output));
        //将思考过的文件放入消息序列中
    }

    public String pull(BlockingQueue<Message> queue,String agentName) throws InterruptedException {
        Message target = queue.take();
        if (target.label.equals (this.name)){
            return target.msg;
        }else {
            return "This is not the corresponding message";
        }
    }

    @Override
    public void run() {

        }

}




