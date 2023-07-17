
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class test1 {
    public static void main(String[] args) throws Exception {
        File source1 = new File("C:\\Users\\20245\\Desktop\\Scientific Research\\Dataset\\anatomy-dataset\\humanTest.owl");
        File source2 = new File("C:\\Users\\20245\\Desktop\\Scientific Research\\Dataset\\anatomy-dataset\\mouseTest.owl");
        String filepath1 = source1.toString();
        String filepath2 = source2.toString();
        String content1 = null;
        String content2 = null;
        Agent agent = new Agent();
        String url = "http://127.0.0.1:8080/toChatGPT";
        String url2 = "http://127.0.0.1:8080/SendToDatabase";
        String url3 = "http://127.0.0.1:8080/";
        try {
            content1 =new String(Files.readAllBytes(Paths.get(filepath1)));
            content2 =new String(Files.readAllBytes(Paths.get(filepath2)));
//            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //根据不同数据类型修改的正则表达式。
        Pattern ontologyClassPattern = Pattern.compile("(<owl:Class[^>]*>.*?</owl:Class>)",Pattern.DOTALL);
        Pattern ontologyPropertyPattern = Pattern.compile("(<owl:ObjectProperty[^>]*>.*?</owl:ObjectProperty>)",Pattern.DOTALL);
        Pattern ontologyDatatypePropertyPattern = Pattern.compile("(<owl:DatatypeProperty[^>]*>.*?</owl:DatatypeProperty>)",Pattern.DOTALL);
        Pattern ontologyIndividualPattern = Pattern.compile("(<owl:NamedIndividual[^>]*>.*?</owl:NamedIndividual>)",Pattern.DOTALL);
        Map<Integer,String> map1 = new HashMap<>();
        Matcher humanMatcher = ontologyClassPattern.matcher(content1);
        Matcher mouseMatcher = ontologyClassPattern.matcher(content2);
        ArrayList<String> humanclasses = new ArrayList<>();
        ArrayList<String> mouseclasses = new ArrayList<>();
        //提取所有ontology中的class component
        while (humanMatcher.find()) {
            humanclasses.add(humanMatcher.group());
        }

        while (mouseMatcher.find()) {
            mouseclasses.add(mouseMatcher.group());
        }
        //初始化向量数据库
//        agent.think(url3+"initiating","humanclasses");
//        for (int i = 0; i < humanclasses.size(); i++) {
//            agent.think(url2,i+","+humanclasses.get(i)+","+"humanclasses");
//        }
//        agent.think(url3+"initiating","mouseclasses");
//        for (int i = 0; i < mouseclasses.size(); i++) {
//            agent.think(url2,i+","+mouseclasses.get(i)+","+"mouseclasses");
//        }
        String mouseclass = mouseclasses.get(0);
        //从向量数据库中匹配语义相近的component
        String result =  agent.think(url3+"query",mouseclass+",humanclasses");
        String[] resultSet = result.split(",");
        System.out.println("--------------------------------------------");
        System.out.println(mouseclass);
        System.out.println("--------------------------------------------");
        System.out.println(resultSet[0]);
        //实例化agent将alignment系统启动。后续我给改成多线程环境中。
        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        Agent agent1 = new Agent("agnet1",mouseclass);
        Agent agent2 = new Agent("agent2",resultSet[0]);
        Context context1 = new Context(agent1,queue);
        Context context2 = new Context(agent2,queue);
        for (int i = 0; i < 3; i++) {
            context1.processing();
            context2.processing();
        }

    }

}


