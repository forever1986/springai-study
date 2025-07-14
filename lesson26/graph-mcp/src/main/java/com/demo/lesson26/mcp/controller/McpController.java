package com.demo.lesson26.mcp.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class McpController {

    private static final Logger logger = LoggerFactory.getLogger(McpController.class);

    private final CompiledGraph compiledGraph;

    public McpController(@Qualifier("mcpGraph") StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping("/graph/mcp")
    public Map<String, Object> mcp(@RequestParam(value = "query", defaultValue = "北京的天气情况？", required = false) String query,
                                    @RequestParam(value = "threadid", defaultValue = "1", required = false) String threadId) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        Optional<OverAllState> invoke = this.compiledGraph.invoke(objectMap, runnableConfig);
        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }

}
