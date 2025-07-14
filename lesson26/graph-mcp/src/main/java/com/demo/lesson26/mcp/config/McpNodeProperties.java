package com.demo.lesson26.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * 为了方便解析配置多少个MCP服务
 */
@ConfigurationProperties(prefix = McpNodeProperties.PREFIX)
public class McpNodeProperties {

    public static final String PREFIX = "spring.ai.graph.nodes";

    private Map<String, Set<String>> node2servers;

    public Map<String, Set<String>> getNode2servers() {
        return node2servers;
    }

    public void setNode2servers(Map<String, Set<String>> node2servers) {
        this.node2servers = node2servers;
    }
}
