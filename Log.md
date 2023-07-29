# AI对齐系统所面临的难点
- **多线程环境(Muti-Thread Environment)**    
在此系统中，我们会面临接口请求时间过长的问题，跟数据库和chatgpt接口的互动的时间花费特别大。这样回导致在处理大体量的ontology的时候会导致时间复杂度过高，多线程环境的目的首先是为了能够同时实例化出不同的agent让他们同时针对一个ontology进行对齐，这样可以在一定程度上增加并发量，从而提高程序的运行效率。实例化出来的不同agent之间是相互独立的，他们之间不会相互影响。但是，他们的资料库中（source）中会被分配同一个ontology中的不同数据，并且独立的放入一个共享的知识库当中，这样可以减少整个程序的运行时间。其次，Java中的Thread接口能够更加细化的控制agent，其中的start()方法能够让agent在一个独立的线程中运行，而run()方法则是在主线程中运行。sleep()方法可以让被实例化的agent对象在一定的时间内休眠，这样可以让agent在一定的时间内不进行对齐，从而增加系统的运行稳定性。但是对于具体的实施，我现在还在探索当中。


- **上下文记忆(Contextual Memory)**  
在此系统中的上下文记忆难点是为了克服gpt接口中的token数量（16k）的最大限制。原本最暴力的解决方法是通过在内存中储存一整条ontology，其次让实例化之后的agent被分配不同的component进行抽象。抽象完成之后的component会降低原本ontology的所需要的token个数，并且以字符串的形式保存在内存中的列表里。这样的好处是可以让chatgpt实现对于整条ontology的理解。在理论上来说更加能帮助chatgpt的理解。但是所面临的问题是即使是被抽象过后的ontology的长度仍然会超过接口的token上限，我们无法将其放入到投入到gpt的prompt中。在此，我们首先将component单独的投入到chatgpt接口之中，然后储存在内存中的列表里。通过后面的余弦相似度的语义搜索来简单地实现整个的系统运行。目前工程上应该可以实现。



- **语义搜索(Semantic Search)**  
正如上文中上下文记忆中所提到，我们无法直接将一整条ontology直接放入投入chatgpt的prompt中，这样导致我们无法让chatgpt掌握一整条ontology中所有的内容。在这种情况下，我们选择具有语义识别能力的余弦相似度算法再结合数据库的存储功能提供记忆的帮助。我们会首先让将gpt处理过后component通过openai的embedding包将结果向量化。然后再将其存储到向量数据库当中。我们再进行语义的validation的时候会根据余弦相似度算法筛选出来语义最相似的两个thought。来进行进一步的验证。我具体的实施方想法是将所有的component都预对齐过后，我们会得到两个充满component和其embedding的数据库。然后通过A->B的检索来进行进一步的validation，如果每一个gpt生成出来的文本对于每一个对齐任务的回答都是肯定，那么我们认为这个系统的对齐是成功的。

- **状态控制(States Control)**
我们所写的Agent在这个类当承担着许多的作用，它需要对component进行理解，对齐component，然后再进行验证。因此它需要很多的状态。为了之后系统的扩展性，我们需要对component进行工程化处理。我们需要创建的类有:agent,context,policy，status。status会被封装在agent里面。我们可以通过policy来控制agent的状态变化。agent又是运行在context这个类中。我们可以在context中启动整个系统然后通过实例化policy来调控agent。通过这种工程手段我们可以降低系统的耦合性并且提升系统的可扩展性。但是现在面临的问题是如果将agent放入context的话，多线程的实现就变得有些模糊。而且如果将agent的状态status作为内部类封装在内部的话会policy可能不太能识别出status这个类。下面是这个类的大概代码。我把prompt都删掉了，格式更加舒服一点。
```java
class agent{
    public class State1{
        public void processing() throws Exception {
            String source = getSource();
            System.out.println("now identifying the message");
            String promptFormat = "\"Your task is to make 
            String thought = think("http://127.0.0.1:8080/toChatGPT", 
            String.format(promptFormat,source));
            System.out.println("sending messages");
            queue.put(new Message(name, thought));
            list.add(thought);
        }
    }



public class State2{
             public class State2{
            public void processing() throws Exception {
                Message take;
                while (true) {
                    take = queue.take();
                    if (!take.label.equals(name)) {
                        String promptFormat ="prompt"
                        System.out.println("the agent is aligning");
                        String thought = 
                        think("http://127.0.0.1:8080/toChatGPT",
                        String.format(promptFormat, list.get(0), take));
                        list.add(thought);
                        queue.put(new Message(name,thought));

                        break;
                    }else {
                        System.out.println("The information will be put 
                        back S");
                        queue.put(take);
                    }
                }

            }
    }
    }
      public class State3{
        public void processing() throws Exception{
            Message take;
            while (true) {
                take = queue.take();
                if (!take.label.equals(name)) {
                    String promptFormat ="prompt"
                    String thought = think("http://127.0.0.1:8080/
                    toChatGPT", String.format(promptFormat, list.get
                    (1), take));
                    list.add(thought);
                    checkText(thought);
                    break;
                }else {
                    System.out.println("this is not corresponding 
                    message");
                    System.out.println(1);
                    queue.put(take);
                }

                System.out.println("the agent will be dead");
            }
        }
    
    
    
    
    
    
    }
```
这是policy类的代码，问题是。这个state类并不能被外部类索引到，也就是这里的state是标红的。所以我还是想要将state分别变成一个单独的类来完成状态的控制。  
```java
public State decideNextState(State currentState) {
        // 判断当前状态并返回下一个状态
        if (currentState instanceof StateA) {
            return new StateB();
        } else if (currentState instanceof StateB) {
            return new StateC();
        } else {
            // 当前状态是StateC，返回StateA，实现循环逻辑
            return new StateA();
        }
    }
```  

----------------------------
以上就是我针对与我们上次的会议内容和自己最近做的工作写出的一点点想法。





















