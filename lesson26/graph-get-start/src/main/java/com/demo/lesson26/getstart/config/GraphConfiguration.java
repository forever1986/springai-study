package com.demo.lesson26.getstart.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.demo.lesson26.getstart.node.ExpanderNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class GraphConfiguration {


    @Bean
    public StateGraph simpleGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        // 全局变量的替换策略（ReplaceStrategy为替换，AppendStrategy为追加）
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            // 用户输入
            keyStrategyHashMap.put("query", new ReplaceStrategy());
            keyStrategyHashMap.put("expandernumber", new ReplaceStrategy());
            keyStrategyHashMap.put("expandercontent", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        // 构造图
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                // 节点ExpanderNode
                .addNode("expander", AsyncNodeAction.node_async(new ExpanderNode(chatClientBuilder)))
                // 边：START -> ExpanderNode
                .addEdge(StateGraph.START, "expander")
                // 边：ExpanderNode -> END
                .addEdge("expander", StateGraph.END);

        // 将图打印出来，可以使用 PlantUML 插件查看
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "expander flow");
        System.out.println("\n=== expander UML Flow ===");
        System.out.println(representation.content());
        System.out.println("==================================\n");

        return stateGraph;
    }
}