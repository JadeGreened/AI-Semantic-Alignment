package de.uni_mannheim.informatik.dws.melt.demomatcher;

import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;
import java.util.Properties;

import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.*;

public class Test {


/*
重写的整个Matcher类,可以放到整个的接口中去evaluate,这里面还有一些(很多)bug我还没来的急调整,
明天有早九TAT,世尧你如果实在看得不顺眼的话可以调整一下，运行一下的话就可以看到报错。
 */
    public static class GPTAlignment extends MatcherYAAAJena {




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
        private void initiatieTheSource(OntModel target, ArrayList<String> sourceList) {
            for (OntClass ontClass : target.listClasses().toList()) {
                //如果是一个匿名类，这里要拿来用的话是非常困难的，一般对齐当中也不会考虑匿名类
                //当然匿名类里面也会有很多的信息，可以拿来使用。这个就要看后续的算法优化了
                if (ontClass.isAnon()) {
                    System.out.println("This is a anonymous class--------------------------------");
                    System.out.println("Uri : " + ontClass.asClass().getURI());
                    for (Iterator<OntClass> i = ontClass.listSubClasses(); i.hasNext(); ) {
                        OntClass subClass = i.next();
                        System.out.println("anon subclass: " + subClass.getURI());
                        //子类
                    }
                    for (Iterator<OntClass> i = ontClass.listSuperClasses(); i.hasNext(); ) {
                        OntClass superClass = i.next();
                        System.out.println("anon superclass: " + superClass.getURI());
                        //超类
                    }
                    for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
                        Statement stmt = i.next();
                        System.out.println("Property: " + stmt.getPredicate().getLocalName());
                        System.out.println("Value: " + stmt.getObject().toString());
                        //属性
                    }
                } else {
                    //如果是一个具名类
                    System.out.println("-----------------------------------------");
                    String info = "";
                    String uri = ontClass.getURI();
                    System.out.println("Class  URI: " + uri);
                    info += "Class  URI: " + uri + "\n";

                    // 获取并打印类的标签
                    String label = ontClass.getLabel(null);
                    System.out.println("Label: " + label);
                    info += "Label: " + label + "\n";
                    //所有的属性
                    for (StmtIterator i = ontClass.listProperties(); i.hasNext(); ) {
                        Statement stmt = i.next();
                        System.out.println("Property: " + stmt.getPredicate().getLocalName());
                        System.out.println("Value: " + stmt.getObject().toString());
                        info += "Property: " + stmt.getPredicate().getLocalName() + "\n";
                        info += "Value: " + stmt.getObject().toString() + "\n";
                    }
                    sourceList.add(info);

                }
            }
        }

        @Override
        public Alignment match(OntModel ontModel, OntModel ontModel1, Alignment alignment, Properties properties) throws Exception {
            OpenAI openAI = new OpenAI();
            String prompt = "<Problem Definition>\n" +
                    "In this task, we are given two ontologies in the form of Relation(Subject, Object), which\n" +
                    "consist of classes and properties.\n" +
                    "<Ontologies Triples>\n" +
                    "[Ontology 1:Ontology2]:%s\n" +
                    "    Do you think these two component are aligned? If so, please output:yes, otherwise, please output:no(just\"yes\" or \"no\", small character no other symbols required) ";
            String className = "ontology";
            OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            OntModel target = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            source.read("D:\\WorkSpace\\projects\\sealsproj\\simpleSealsMatcher\\src\\main\\java\\DataSet\\human.owl");
            target.read("D:\\WorkSpace\\projects\\sealsproj\\simpleSealsMatcher\\src\\main\\java\\DataSet\\mouse.owl");
            ArrayList<String> SourceList = new ArrayList<>();
            ArrayList<String> TargetList = new ArrayList<>();

            // 获取一个类
            initiatieTheSource(source, SourceList);
            initiatieTheSource(target, TargetList);
            Zilliz.initiatingDataBase();
            try {
                Zilliz.insertData(className,TargetList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            /*
            这里还是按照原来的算法来的，
            先语义搜索到相似的class，然后再进行对齐，如果我们的整个模型测出来的效果不好的话，可以用暴力遍历。
             */
            for (String s : SourceList) {
                List<String> query = Zilliz.query(className, s);
                for (String s1 : query) {
                    String tuple = "[%s,%s]";
                    String ontologies = String.format(tuple,s,s1);
                    String input = String.format(prompt, ontologies);
                    String thought = openAI.think(input);
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
    }

    public static void main(String[] args) {
        testOntModelProperties();
//        runMatcher();
    }

    private static void runMatcher(){
        // let's initialize our matcher
        GPTAlignment myMatcher = new GPTAlignment();

        // let's execute our matcher on the OAEI Anatomy test case
        ExecutionResultSet ers = Executor.run(TrackRepository.Anatomy.Default.getFirstTestCase(), myMatcher);

        // let's evaluate our matcher (you can find the results in the `results` folder (will be created if it
        // does not exist).
        EvaluatorCSV evaluatorCSV = new EvaluatorCSV(ers);
        evaluatorCSV.writeToDirectory();
    }

    private static void testOntModelProperties(){
        OntModel source = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        source.read("/Users/shiyaozhang/Developer/AI-Semantic-Alignment/simpleSealsMatcher/src/main/java/DataSet/human.owl");
        OntClass var = source.listClasses().next();
        print("var==================");
        print(var.getLabel(null));
        print(var.getURI());
        print(var.getLocalName());
        print(var.getComment(null));
        print(var.getNameSpace());
        print(var.getVersionInfo());
        if (var.getEquivalentClass() != null){
            print("equivalent class ================");
            print(var.getEquivalentClass().getURI());
            print(var.getEquivalentClass().getLocalName());
            print(var.getEquivalentClass().getLabel(null));
            print(var.getEquivalentClass().getComment(null));
        }
        if(var.getSubClass() != null){
            print("subclass========================");
            print(var.getSubClass().getURI());
            print(var.getSubClass().getLocalName());
            print(var.getSubClass().getLabel(null));
            print(var.getSubClass().getComment(null));
        }
        if(var.getSuperClass() != null){
            print("superclass========================");
            print(var.getSuperClass().getURI());
            print(var.getSuperClass().getLocalName());
            print(var.getSuperClass().getLabel(null));
            print(var.getSuperClass().getComment(null));
        }

        print(var.getSameAs() == null ? "null" : var.getSameAs().toString());
        print(var.getDisjointWith() == null ? "null" : var.getDisjointWith().toString());
    }

    private static void print(String s){
        System.out.println(s);
    }
}


















