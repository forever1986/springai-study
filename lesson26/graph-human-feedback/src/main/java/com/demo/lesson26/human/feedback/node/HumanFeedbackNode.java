package com.demo.lesson26.human.feedback.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.HashMap;
import java.util.Map;

public class HumanFeedbackNode implements NodeAction {


    @Override
    public Map<String, Object> apply(OverAllState state) {
        System.out.println("humanfeedback node is running.");

        HashMap<String, Object> resultMap = new HashMap<>();
        String nextStep = StateGraph.END;
        // 获取OverAllState中humanFeedback参数的值
        Map<String, Object> feedBackData = state.humanFeedback().data();
        // 判断如果是true，则将humannextnode设置为TranslateNode节点的ID，如果是flase，则将humannextnode设置为END节点
        boolean feedback = (boolean) feedBackData.getOrDefault("feedback", true);
        if (feedback) {
            nextStep = "translate";
        }
        resultMap.put("humannextnode", nextStep);

        System.out.println("humanfeedback node -> "+ nextStep+" node");
        return resultMap;
    }
}