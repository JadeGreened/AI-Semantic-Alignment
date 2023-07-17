import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.text.StringEscapeUtils;

import java.util.concurrent.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.regex.*;

class test {
    public static void main(String[] args) throws Exception {
        File source1 = new File("C:\\Users\\20245\\Desktop\\Scientific Research\\Dataset\\anatomy-dataset\\human.owl");
        File source2 = new File("C:\\Users\\20245\\Desktop\\Scientific Research\\Dataset\\anatomy-dataset\\mouse.owl");
        String filepath1 = source1.toString();
        String filepath2 = source2.toString();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filepath1)));
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }



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

//        String prompt1 = "\"Your task is to make clarifications about the ontology I gave you. You can cite the original text to make explanations. Make the complete identification finally make it short\" +\n" +
//                "                \"and sweet. The clarification should be around 30 words.Please make the definition you provide easier to judge for other artificial intelligence models.\\n ontology:%sThe structure of the output should be like this: Explanation:<your explanation>\\nDefinition:<your definition>\"";
//        String prompt2 = "I will give you definition and explanation of two ontologies in different Knowledge network. Your task is to judge the two ontology are the same thing." +
//                "Just say yes and no for the judgement and show me the reason.\n Here is the infomation: ontology1:%s,ontology2:%s.\n The output should be structured like this" +
//                ": Answer:<your answer>, Explanation:<your explanation>";

        BlockingQueue<String> queue2 = new LinkedBlockingQueue<>();
        Agent agent1 = new Agent("agent1", queue1, queue2, xmlInput1);
        Agent agent2 = new Agent("agent2", queue2, queue1, xmlInput2);
//        String thought1 = agent1.think(url1,  "Your task is to make clarifications about the ontology I gave you. You can cite the original text to make explanations. Make the complete identification finally make it short" +
//                "and sweet. The clarification should be around 30 words.Please make the definition you provide easier to judge for other artificial intelligence models.\n ontology:"+xmlInput1+"The structure of the output should be like this: Explanation:<your explanation>\nDefinition:<your definition>");
//        String thought2 = agent2.think(url1,  xmlInput2 + "Your task is to make clarifications about the ontology I gave you. You can cite the original text to make explanations. Make the complete identification finally make it short" +
//                     "and sweet. The clarification should be around 30 words. Please make the definition you provide easier to judge for other artificial intelligence models.\n ontology:"+xmlInput2+"The structure of the output should be like this: Explanation:<your explanation>\nDefinition:<your definition>");



        String A = "You are a generative agnet that is capable of explaning the ontology component.You are supposed to give a clear explanation and definition about the ontology component provided.component={%s}. The definition is the main basis for other agents to judge, please make it decisive.You can cite the original text to make explanations.The clarification should be within 50 words. And it should be easily judeged by other Agent.The format should be like following:explanation:<your explanation>. definition:<your definition>\"";
        String B = "You are a generative agent that is capable of comparing the different ontology component explanation in different Knowledge network that is generated by other agents. You are supposed to judge the two ontology are the same thing.Just say yes and no for the judgement and show the reason.The information is listed below:ontology1:%s,ontology2:%s.The out put should be structured like this :Answer:<your answer>,Explanation:<Your Explanation>";
        String thought1 = "Explanation: The ontology component provided is an ObjectProperty called \"getsFeedbackFrom\". It defines a relation between a controlled system and the entities (usually sensors) that provide feedback about its action. The range of the relation is a System, while the domain is a ControlledSystem. It also has an inverseOf relation defined with \"providesFeedbackTo\"." +
                "Definition: \"GetsFeedbackFrom\" is an ObjectProperty in the IoT-O ontology that specifies the relation between a ControlledSystem and the entities that provide feedback about its action, typically sensors. It has a range of System and a domain of ControlledSystem, and is inversely related to \"providesFeedbackTo\".";
        String thought2 = "Explanation: The ontology component provided is an OWL annotation property, named \"relativeLocation\". This property is used to describe the location of a device using Geonames URIs to represent a place, city, area, or region. It has a range of xsd:string and a domain of geo:Point." +
                "Definition: \"relativeLocation\" is an OWL annotation property used to indicate the location of a device using Geonames URIs to represent a place, city, area, or region. It has a range of xsd:string and a domain of geo:Point.";
        String test = "You are a generative artificial intelligence with the ability to align IoT ontology component and to instruct the operation of a system. You are supposed to judge the two component are the same.If so, answer yes, No otherwise. And then show me the reason.The information is listed below:ontology1:%s,ontology2:%s. Your output will be used to instruct a software system. Next you are supposed to update your contextual memory, decide what to do next from options a) store the infomation in a context memory b)call for the chatgpt interface for further information? You have to choose one and the resopnses should be the same I give you. The output should be structured like this :Answer:<your answer>, Explanation:<Your Explanation>，NextStep:<your Nextstep>.";
//        String target1 = String.format(A , xmlInput1);
//        String target2 = String.format(A , xmlInput2);
//        String output1 = agent1.think(url1, thought1);
//        String output2 = agent2.think(url1, target2);
        String test1 = String.format(test,thought1,thought2);
        String test2 = String.format(test,thought1,thought2);
//        System.out.println(test1);
//        String p1 = agent1.think(url1 , test1);
//        String p2 = agent2.think(url1, test2);

        String test3 = "You are a generative artificial intelligence with the ability to negotiate with other generative agent. You will receive two groups of summary of the IoT ontology component in different knowledge net.The data is listed below group1:{%s}, group2:{%s}. Your tasks are listed as following.When the answers is not equal(equal means the same ), you will need to deal with the first group, make further explaination and check if the the alignment is logically correct.If so, answer \"Indeed yes\". When the answer is equal ,you will need to double check the outcome and answer \"Indeed no\". Your answer should be structured like this Indeed Yes/No. (The explanation is not required).";
        String test4 = "You are a generative artificial agent with the ability to negotiate with other generative agent. You will receive two groups of summary of the IoT ontology component in different knowledge net.The infomation is listed below group1:{%s}, group2:{%s}. Your tasks are listed as following.When the answers is equal(equal means the same ), you will need to deal with these groups, make further explaination and check if the the alignment is logically correct.If so, answer \\\"Indeed yes\\\".In this senario,the explanation is not required. When the answer is not equal ,you will need to double check the outcome and answer \"Indeed no\" and make explanation.";
        //这是一个比较细化的prompt，旨在探索交谈的可能性。
        String test5 = "You are a generative agent that is capable of judging the ontology component . Your task is to give a precise comment on the ontology. Here is the following requirement: \n" +
                "{1.Describe the data type of the component. 2.Then describe the properties that is contains. 3.At last try to explain the terminology that relate to it.} If you did not find any component and do not have enough infomation just say \"lacking context\". Here is the component:{%s}The structure of the output should be like this: Data type:<your datatype>, Properties:<the describtion>, Terminology Explanation:<your explanation>. \n";
        String test6 = "";
//        String Test4 = String.format(test4,p1,p2);
//        String Test3 = String.format(test3,p1,p2);
        String Test5 = String.format(test5,xmlInput1);
        String Test6 = String.format(test5,xmlInput2);
        String test7 = "You are a generative agent that is capable of aligning ontology component. Your task is to compare the two ontology component in the perspectives of data type terminology and properties. If you think these two ontologies are likely to be the same thing in different knowledge net then express the positive attitude. If not, do opposite and tell the difference between them. If you think it lacks information. Just say \"lacking context.\"Here is the two groups of thoughts of ontologies:group1:{%s},group2:{%s}. The structure of the output should be like this: Answer: <your answer>, difference(if necessary) <your difference>\n" ;
        String Thought1 = agent1.think(url1, Test5);
        String Thought2 = agent1.think(url1, Test6);
        String alignment1 = String.format(test7,thought1,thought2);
        String Resutlt1 = agent1.think(url1, alignment1);
        String Resutlt2 = agent1.think(url1, alignment1);

        String test8 = "You are a generative agent that is capable of negotiating with other generative agent like you. Here is some thoughts based on these two ontology1:{%s}, ontology2:{%s}, Here is thought from agent1 :{%s}. Your task is reconfirm the thought. Here is your task: you should figure out these ontology components(no need to output your analysis) and then check if the thought from agent1 make sense. If so, You output should be like this: Answer:Agreed(Just say Agreed!!! No explanaton needed!!). If not, please answer: Disagreed and make explanations about your judgement.In this situation, You output should be structured like this: Answer: <your answer>, Explanation: <Your explanation>.";
        String Test8 = String.format(test8,xmlInput1,xmlInput2,Resutlt1);
        agent1.think(url1 , Test8);




        //            agent1.setThoughts(thought1);
//            agent2.setThoughts(thought2);
//            agent1.start();
//            agent2.start();
//            // 交流的回合数
//            for (int i = 0; i < 3; i++) {
//                agent1.run();
//                agent2.run();
    }
}








