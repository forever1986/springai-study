package com.demo.lesson26.human.feedback.controller;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverConstant;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class GraphHumanController {

    private final CompiledGraph compiledGraph;

    @Autowired
    public GraphHumanController(@Qualifier("humanGraph") StateGraph stateGraph) throws GraphStateException {
        SaverConfig saverConfig = SaverConfig.builder().register(SaverConstant.MEMORY, new MemorySaver()).build();
        this.compiledGraph = stateGraph.compile(CompileConfig.builder()
                .saverConfig(saverConfig)
                .interruptBefore("humanfeedback") // 关键点：在humanfeedback打断流程
                .build());
    }

    @GetMapping("/graph/human/expand")
    public Map<String, Object> expand(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？", required = false) String query,
                                                @RequestParam(value = "expandernumber", defaultValue = "3", required = false) Integer expanderNumber,
                                                @RequestParam(value = "threadid", defaultValue = "1", required = false) String threadId) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expandernumber", expanderNumber);
        Optional<OverAllState> invoke = compiledGraph.invoke(objectMap,runnableConfig);
        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }

    @GetMapping("/graph/human/resume")
    public Map<String, Object> resume(@RequestParam(value = "threadid", defaultValue = "1", required = false) String threadId,
                                                @RequestParam(value = "feedback", defaultValue = "true", required = false) boolean feedBack) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();

        // 重新加载threadid=1的stateSnapshot
        StateSnapshot stateSnapshot = this.compiledGraph.getState(runnableConfig);
        OverAllState state = stateSnapshot.state();
        state.withResume();// 设置resume标志，表示从snapshot开始继续

        // 添加新的参数feedback
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("feedback", feedBack);
        state.withHumanFeedback(new OverAllState.HumanFeedback(objectMap, ""));

        // 调用执行，入参是从snapshot中重新加载的OverAllState，并且添加了feedback参数
        Optional<OverAllState> invoke = compiledGraph.invoke(state,runnableConfig);

        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }

}
