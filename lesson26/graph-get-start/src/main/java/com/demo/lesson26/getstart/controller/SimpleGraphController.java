package com.demo.lesson26.getstart.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class SimpleGraphController {

    private final CompiledGraph compiledGraph;

    public SimpleGraphController(@Qualifier("simpleGraph") StateGraph stateGraph) throws GraphStateException {
        // 将图编译成CompiledGraph
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping(value = "/graph/expand")
    public Map<String, Object> expand(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？", required = false) String query,
                                      @RequestParam(value = "expandernumber", defaultValue = "3", required = false) Integer  expanderNumber,
                                      @RequestParam(value = "threadid", defaultValue = "ceshi", required = false) String threadId) throws GraphRunnerException {
        // 构建会话配置
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        // 入参配置
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expandernumber", expanderNumber);
        // 调用图
        Optional<OverAllState> invoke = this.compiledGraph.invoke(objectMap, runnableConfig);
        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }

}
