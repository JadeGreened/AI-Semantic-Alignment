// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.text.StringEscapeUtils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
public class Main {

    public static void main(String[] args) throws Exception {

//        Agent agent1 = new Agent();
//        Agent agent2 = new Agent();
//        String resource = "";
//        String url1 = "http://127.0.0.1:8080/toChatGPT";
//        String url2 = "http://127.0.0.1:8080/query";
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
//        String encodedXmlInput = Base64.getEncoder().encodeToString(xmlInput1.getBytes(StandardCharsets.UTF_8));
//        String jsonInput = "{\"ontology\":\"" + encodedXmlInput + "\"}";
//        try {
//            resource = agent1.think(url2, jsonInput);
//            System.out.println(resource);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //prompt engineering
//        String prompt = "{\"ontology\":\""+"请你先用base64解码这段文字："+encodedXmlInput+"，然后说说你认为这个ontology在干什么\"}";
//        System.out.println(prompt);
//        String thought = agent1.think(url1, prompt);
//        System.out.println(thought);
//
////        String thought = agent1.think(url1,"{\"ontology\":\""+resource+"!\"}");
//
////        getUnderstanding(thought);
////        String chineseStr = unicodeToCn(thought);
////        System.out.println(thought.getClass());
////        System.out.println(thought);
//
////        System.out.println(chineseStr);
////
////
//        BlockingQueue<String> queue1 = new LinkedBlockingQueue<>();
//        BlockingQueue<String> queue2 = new LinkedBlockingQueue<>();
//
//        Thread Agent1 = new Thread(new Agent("Agent 1", queue1, queue2));
//        Thread Agent2 = new Thread(new Agent("Agent 2", queue2, queue1));
//
//        agent1.start();
//        agent2.start();
//
//        try {
//            queue1.put("1");  // 开始对话
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        Agent agent1 = new Agent("agnet1",xmlInput1);
        Agent agent2 = new Agent("agent2",xmlInput2);
        Context context1 = new Context(agent1,queue);
        Context context2 = new Context(agent2,queue);
        for (int i = 0; i < 3; i++) {
            context1.processing();
            context2.processing();
        }

    }
    }








