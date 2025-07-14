package com.demo.lesson26.parallel.controller;

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
public class ParallelController {

    private static final Logger logger = LoggerFactory.getLogger(ParallelController.class);

    private final CompiledGraph compiledGraph;

    public ParallelController(@Qualifier("parallelGraph") StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping(value = "/graph/parallel/expand-translate")
    public Map<String, Object> expandAndTranslate(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？", required = false) String query,
                                                  @RequestParam(value = "expandernumber", defaultValue = "3", required = false) Integer  expanderNumber,
                                                  @RequestParam(value = "translatelanguage", defaultValue = "english", required = false) String translateLanguage,
                                                  @RequestParam(value = "threadid", defaultValue = "1", required = false) String threadId) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expandernumber", expanderNumber);
        objectMap.put("translatelanguage", translateLanguage);

        Optional<OverAllState> invoke = this.compiledGraph.invoke(objectMap, runnableConfig);

        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }

}
