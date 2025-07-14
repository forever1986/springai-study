package com.demo.lesson26.mcp.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.demo.lesson26.mcp.node.McpNode;
import com.demo.lesson26.mcp.tool.McpClientToolCallbackProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties({ McpNodeProperties.class })
public class McpGaphConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(McpGaphConfiguration.class);

    @Autowired
    private McpClientToolCallbackProvider mcpClientToolCallbackProvider;

    @Bean
    public StateGraph mcpGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

            // 用户输入
            keyStrategyHashMap.put("query", new ReplaceStrategy());
            keyStrategyHashMap.put("mcpcontent", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        // 构建图
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode("mcp", AsyncNodeAction.node_async(new McpNode(chatClientBuilder, mcpClientToolCallbackProvider)))

                .addEdge(StateGraph.START, "mcp")
                .addEdge("mcp", StateGraph.END);

        // 添加 PlantUML 打印
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "mcp flow");
        logger.info("\n=== mcp UML Flow ===");
        logger.info(representation.content());
        logger.info("==================================\n");

        return stateGraph;
    }
}
