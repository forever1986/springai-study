package com.demo.lesson26.parallel.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.Map;

public class MergeResultsNode implements NodeAction {

    @Override
    public Map<String, Object> apply(OverAllState state) {
        Object expanderContent = state.value("expandercontent").orElse("unknown");
        String translateContent = (String) state.value("translatecontent").orElse("");

        return Map.of("mergeresult", Map.of("expandercontent", expanderContent,
                "translatecontent", translateContent));
    }

}
