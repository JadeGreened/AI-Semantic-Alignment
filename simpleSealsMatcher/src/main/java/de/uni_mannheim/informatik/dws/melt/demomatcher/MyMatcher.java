package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyMatcher extends MatcherYAAAJena {
    private static final String CLASS_NAME = "ontology";

    @Override
    public Alignment match(OntModel source, OntModel target, Alignment inputAlignment, Properties properties) throws Exception {
        // setup
        Alignment alignment = new Alignment();
        OpenAI openAI = new OpenAI();

        // 获取一个类
        ArrayList<String> sourceList = toArrayList(source);
        ArrayList<String> targetList = toArrayList(target);

        Zilliz.initiatingDataBase();
        try {
            Zilliz.insertData(CLASS_NAME,targetList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
            /*
            这里还是按照原来的算法来的，
            先语义搜索到相似的class，然后再进行对齐，如果我们的整个模型测出来的效果不好的话，可以用暴力遍历。
             */
        for (String s : sourceList) {
            List<String> query = Zilliz.query(CLASS_NAME, s);
            for (String s1 : query) {
                String thought = openAI.comepareComponenties(s, s1);
                System.out.println(thought);
                if (thought.equals("yes")){
                    String uriSource = getURI(s);
                    String uriTarget = getURI(s1);
                    alignment.add(uriSource,uriTarget);
                }else if (thought.equals("Yes")){
                    String uriSource = getURI(s);
                    String uriTarget = getURI(s1);
                    alignment.add(uriSource,uriTarget);
                }
            }
        }
        return alignment;
    }

    private String getURI(String data){
        // 找到 "Class  URI: " 和 "Label: " 的位置
        int startIndex = data.indexOf("Class  URI: ") + "Class  URI: ".length();
        int endIndex = data.indexOf("Label: ");

        // 使用 substring 提取 URI
        String classUri = data.substring(startIndex, endIndex).trim();

        // 将字符串转为 URI
        return classUri;
    }

    /*
    这里是用的jena的api接口来处理的owl文件，这样操作比较方便，而且能直接拿到class的URI（Uniform Resource Identifier）。并且也能找到一个ontology的全部属性和信息，
    如果你觉得不好的话可以改。
     */
    private ArrayList<String> toArrayList(OntModel ontology) {
        print("=========================================");
        print("Start transfer ontology to ArrayList");

        ArrayList<String> list = new ArrayList<>();
        for (OntClass ontClass : ontology.listClasses().toList()) {
            //如果是一个匿名类，这里要拿来用的话是非常困难的，一般对齐当中也不会考虑匿名类
            //当然匿名类里面也会有很多的信息，可以拿来使用。这个就要看后续的算法优化了
            if (ontClass.isAnon()) {
                // currently just ignored, so comment out
////                System.out.println("This is a anonymous class--------------------------------");
////                System.out.println("Uri : " + ontClass.asClass().getURI());
//                for (Iterator<OntClass> i = ontClass.listSubClasses(); i.hasNext(); ) {
//                    OntClass subClass = i.next();
////                    System.out.println("anon subclass: " + subClass.getURI());
//                    //子类
//                }
//                for (Iterator<OntClass> i = ontClass.listSuperClasses(); i.hasNext(); ) {
//                    OntClass superClass = i.next();
////                    System.out.println("anon superclass: " + superClass.getURI());
//                    //超类
//                }
//                for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
//                    Statement stmt = i.next();
////                    System.out.println("Property: " + stmt.getPredicate().getLocalName());
////                    System.out.println("Value: " + stmt.getObject().toString());
//                    //属性
//                }
            } else {
                //如果是一个具名类
//                System.out.println("-----------------------------------------");
                String info = "";
                String uri = ontClass.getURI();
//                System.out.println("Class  URI: " + uri);
                info += "Class  URI: " + uri + "\n";

                // 获取并打印类的标签
                String label = ontClass.getLabel(null);
//                System.out.println("Label: " + label);
                info += "Label: " + label + "\n";
                //所有的属性
                for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
                    Statement stmt = i.next();
//                    System.out.println("Property: " + stmt.getPredicate().getLocalName());
//                    System.out.println("Value: " + stmt.getObject().toString());
                    info += "Property: " + stmt.getPredicate().getLocalName() + "\n";
                    info += "Value: " + stmt.getObject().toString() + "\n";
                }
                list.add(info);
            }
            print(ontClass.getURI());
        }
        return list;
    }

    private static void print(String s){
        System.out.println(s);
    }
}
