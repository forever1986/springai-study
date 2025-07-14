package com.demo.lesson26.human.feedback.edge;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

import java.util.Map;

/**
 * 条件边
 */
public class HumanFeedbackEdge implements EdgeAction {

    @Override
    public String apply(OverAllState state) throws Exception {
        // 获取OverAllState的key=humannextnode的值，这个值在该边的上一个节点HumanFeedbackNode中设置
        return (String) state.value("humannextnode", StateGraph.END);
    }

}