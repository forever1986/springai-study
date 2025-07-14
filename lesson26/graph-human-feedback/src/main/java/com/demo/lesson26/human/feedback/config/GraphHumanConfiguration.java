package com.demo.lesson26.human.feedback.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.demo.lesson26.human.feedback.edge.HumanFeedbackEdge;
import com.demo.lesson26.human.feedback.node.ExpanderNode;
import com.demo.lesson26.human.feedback.node.HumanFeedbackNode;
import com.demo.lesson26.human.feedback.node.TranslateNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class GraphHumanConfiguration {

    @Bean
    public StateGraph humanGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        // 全局变量的替换策略（ReplaceStrategy为替换，AppendStrategy为追加）
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            // 用户输入
            keyStrategyHashMap.put("query", new ReplaceStrategy());
            keyStrategyHashMap.put("threadid", new ReplaceStrategy());

            keyStrategyHashMap.put("expandernumber", new ReplaceStrategy());
            keyStrategyHashMap.put("expandercontent", new ReplaceStrategy());

            // 人类反馈
            keyStrategyHashMap.put("feedback", new ReplaceStrategy());
            keyStrategyHashMap.put("humannextnode", new ReplaceStrategy());

            // 是否需要翻译
            keyStrategyHashMap.put("translatelanguage", new ReplaceStrategy());
            keyStrategyHashMap.put("translatecontent", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        // 构造图
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                // 节点ExpanderNode
                .addNode("expander", AsyncNodeAction.node_async(new ExpanderNode(chatClientBuilder)))
                // 节点TranslateNode
                .addNode("translate", AsyncNodeAction.node_async(new TranslateNode(chatClientBuilder)))
                // 节点HumanFeedbackNode
                .addNode("humanfeedback", AsyncNodeAction.node_async(new HumanFeedbackNode()))
                // 边：START -> ExpanderNode
                .addEdge(StateGraph.START, "expander")
                // 边：ExpanderNode -> HumanFeedbackNode
                .addEdge("expander", "humanfeedback")
                // 条件边：参数humanfeedback为true，则HumanFeedbackNode -> TranslateNode; 否则HumanFeedbackNode -> END
                .addConditionalEdges("humanfeedback", AsyncEdgeAction.edge_async((new HumanFeedbackEdge())), Map.of(
                        "translate", "translate", StateGraph.END, StateGraph.END))
                // 边：TranslateNode -> END
                .addEdge("translate", StateGraph.END);

        // 将图打印出来，可以使用 PlantUML 插件查看
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "human flow");
        System.out.println("\n=== expander UML Flow ===");
        System.out.println(representation.content());
        System.out.println("==================================\n");

        return stateGraph;
    }
}