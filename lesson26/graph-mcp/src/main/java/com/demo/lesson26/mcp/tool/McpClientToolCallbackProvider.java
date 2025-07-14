package com.demo.lesson26.mcp.tool;

import com.demo.lesson26.mcp.config.McpNodeProperties;
import org.apache.commons.compress.utils.Lists;
import org.glassfish.jersey.internal.guava.Sets;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.client.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class McpClientToolCallbackProvider {

    private final ToolCallbackProvider toolCallbackProvider;

    private final McpClientCommonProperties commonProperties;

    private final McpNodeProperties mcpNodeProperties;

    public McpClientToolCallbackProvider(ToolCallbackProvider toolCallbackProvider,
                                         McpClientCommonProperties commonProperties, McpNodeProperties mcpNodeProperties) {
        this.toolCallbackProvider = toolCallbackProvider;
        this.commonProperties = commonProperties;
        this.mcpNodeProperties = mcpNodeProperties;
    }

    /**
     * 通过配置的spring.ai.graph.nodes的名称，获取从Spring AI中获取到的toolCall
     */
    public Set<ToolCallback> findToolCallbacks(String nodeName) {

        // 获取配置文件中spring.ai.graph.nodes开头的数据
        Set<ToolCallback> defineCallback = Sets.newHashSet();
        Set<String> mcpClients = mcpNodeProperties.getNode2servers().get(nodeName);
        if (mcpClients == null || mcpClients.isEmpty()) {
            return defineCallback;
        }

        List<String> exceptMcpClientNames = Lists.newArrayList();
        for (String mcpClient : mcpClients) {
            // my-mcp-client
            String name = commonProperties.getName();
            // mymcpclientserver1
            String prefixedMcpClientName = McpToolUtils.prefixedToolName(name, mcpClient);
            exceptMcpClientNames.add(prefixedMcpClientName);
        }

        // 从Spring AI的MCP客户端获取到的toolCall，放到defineCallback，以方便注册到MCPNode中的chatClient
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        for (ToolCallback toolCallback : toolCallbacks) {
            ToolDefinition toolDefinition = toolCallback.getToolDefinition();
            String name = toolDefinition.name();
            for (String exceptMcpClientName : exceptMcpClientNames) {
                if (name.startsWith(exceptMcpClientName)) {
                    defineCallback.add(toolCallback);
                }
            }
        }
        return defineCallback;
    }
}
