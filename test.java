import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.text.StringEscapeUtils;

import java.util.concurrent.*;

class test {
//  static class Agent implements Runnable {
//        private final BlockingQueue<Integer> queue;
//        private final BlockingQueue<Integer> responseQueue;
//        private final String name;
//        private final int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//
//        Agent(String name, BlockingQueue<Integer> queue, BlockingQueue<Integer> responseQueue) {
//            this.name = name;
//            this.queue = queue;
//            this.responseQueue = responseQueue;
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    Integer receivedNumber = queue.take(); // 如果队列为空，会阻塞当前线程
//                    System.out.println(name + " received: " + receivedNumber);
//
//                    int randomNumberIndex = (int) (Math.random() * numbers.length);
//                    responseQueue.put(numbers[randomNumberIndex]);  // 如果队列满，会阻塞当前线程
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//        }
//    }
//
//
//        public static void main(String[] args) {
//            BlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>();
//            BlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>();
//
//            Thread agent1 = new Thread(new Agent("Agent 1", queue1, queue2));
//            Thread agent2 = new Thread(new Agent("Agent 2", queue2, queue1));
//
//            agent1.start();
//            agent2.start();
//
//            try {
//                queue1.put(1);  // 开始对话
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }


    class GPTModel extends Thread {
        private final BlockingQueue<String> inputQueue;
        private final BlockingQueue<String> outputQueue;

        public GPTModel(BlockingQueue<String> inputQueue, BlockingQueue<String> outputQueue) {
            this.inputQueue = inputQueue;
            this.outputQueue = outputQueue;
        }

        public void run() {
            while (true) {
                try {
                    String input = inputQueue.take();
                    // 在这里，我们只是将接收到的消息转化为大写来模拟GPT模型的响应。
                    String response = input.toUpperCase();
                    outputQueue.put(response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        String url1 = "http://127.0.0.1:8080/toChatGPT";
        String url2 = "http://127.0.0.1:8080/query";
        String xmlInput1 = "<ObjectProperty rdf:about=\"http://www.irit.fr/recherches/MELODI/ontologies/IoT-O#getsFeedbackFrom\">\n" +
                "        <inverseOf rdf:resource=\"http://www.irit.fr/recherches/MELODI/ontologies/IoT-O#providesFeedbackTo\"/>\n" +
                "        <rdfs:domain rdf:resource=\"http://www.irit.fr/recherches/MELODI/ontologies/IoT-O#ControlledSystem\"/>\n" +
                "        <rdfs:range rdf:resource=\"http://purl.oclc.org/NET/ssnx/ssn#System\"/>\n" +
                "        <rdfs:comment>Relation between a controlled system and the entities that provide it with a feedback about its action, usually sensors.</rdfs:comment>\n" +
                "        <rdfs:label>gets feedback from</rdfs:label>\n" +
                "    </ObjectProperty>";
            String xmlInput2 = "<owl:AnnotationProperty rdf:about=\"&iot-lite;relativeLocation\">\n" +
                    "         <rdfs:comment xml:lang=\"en\">Relative Location is used to provide a place for where the Device is in. For example, Geonames URIs can be used to represent a place, city, area, or region. For &quot;University of Surrey&quot; this would be &quot;http://sws.geonames.org/6695971/&quot;</rdfs:comment>\n" +
                    "        <rdfs:range rdf:resource=\"&xsd;string\"/>\n" +
                    "        <rdfs:domain rdf:resource=\"&geo;Point\"/>\n" +
                    "    </owl:AnnotationProperty>";
            BlockingQueue<String> queue1 = new LinkedBlockingQueue<>();
            BlockingQueue<String> queue2 = new LinkedBlockingQueue<>();
            Agent agent1 = new Agent("agent1",queue1,queue2,xmlInput1);
            Agent agent2 = new Agent("agent2",queue2,queue1,xmlInput2);
            String thought1 = agent1.think(url1,   xmlInput1 + "请你解释一下上面的ontology，谈谈你的看法}");
            String thought2 = agent2.think(url1,  xmlInput2 + "请你解释一下上面的ontology，谈谈你的看法");
            agent1.setThoughts(thought1);
            agent2.setThoughts(thought2);
            agent1.start();
            agent2.start();
            // 交流的回合数
            for (int i = 0; i < 3; i++) {
                agent1.run();
                agent2.run();
            }
    }
    }







