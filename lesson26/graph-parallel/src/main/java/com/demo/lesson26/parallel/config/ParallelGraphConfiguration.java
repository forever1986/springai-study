package com.demo.lesson26.parallel.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.demo.lesson26.parallel.node.ExpanderNode;
import com.demo.lesson26.parallel.node.TranslateNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ParallelGraphConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ParallelGraphConfiguration.class);

    @Bean
    public StateGraph parallelGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

            // 用户输入
            keyStrategyHashMap.put("query", new ReplaceStrategy());

            keyStrategyHashMap.put("expandernumber", new ReplaceStrategy());
            keyStrategyHashMap.put("expandercontent", new ReplaceStrategy());

            keyStrategyHashMap.put("translatelanguage", new ReplaceStrategy());
            keyStrategyHashMap.put("translatecontent", new ReplaceStrategy());

            keyStrategyHashMap.put("mergeresult", new ReplaceStrategy());

            return keyStrategyHashMap;
        };

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode("expander", AsyncNodeAction.node_async(new ExpanderNode(chatClientBuilder)))
                .addNode("translate", AsyncNodeAction.node_async(new TranslateNode(chatClientBuilder)))
                .addNode("merge", AsyncNodeAction.node_async(new MergeResultsNode()))

                .addEdge(StateGraph.START, "expander")
                .addEdge(StateGraph.START, "translate")
                .addEdge("translate", "merge")
                .addEdge("expander", "merge")

                .addEdge("merge", StateGraph.END);

        // 添加 PlantUML 打印
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "expander flow");
        logger.info("\n=== expander UML Flow ===");
        logger.info(representation.content());
        logger.info("==================================\n");

        return stateGraph;
    }

    private record MergeResultsNode() implements NodeAction {
        @Override
        public Map<String, Object> apply(OverAllState state) {
            Object expanderContent = state.value("expandercontent").orElse("unknown");
            String translateContent = (String) state.value("translatecontent").orElse("");

            return Map.of("mergeresult", Map.of("expandercontent", expanderContent,
                    "translatecontent", translateContent));
        }
    }
}
