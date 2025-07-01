package com.demo.lesson11.advanced.config;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个RRF算法
 */
public class RRFFusionProcessor {

    private final int rankConstant; // 阻尼常数 k

    public RRFFusionProcessor(int rankConstant) {
        this.rankConstant = rankConstant;
    }

    // 计算多路检索结果的 RRF 融合分数
    public List<Document> fuseRankings(List<List<Document>> rankedLists) {
        Map<String, Double> scoreMap = new HashMap<>();
        Map<String, Document> docMap = new HashMap<>();

        // 遍历每个检索系统结果
        for (List<Document> list : rankedLists) {
            for (int rank = 0; rank < list.size(); rank++) {
                Document doc = list.get(rank);
                String docId = doc.getId(); // 假设 Document 有唯一 ID

                // 记录文档对象（避免重复创建）
                docMap.putIfAbsent(docId, doc);

                // 累加 RRF 分数：1/(k + rank)
                double score = 1.0 / (rankConstant + rank + 1);
                scoreMap.put(docId, scoreMap.getOrDefault(docId, 0.0) + score);
            }
        }

        // 按 RRF 分数降序排序
        List<Document> fusedList = new ArrayList<>(docMap.values());
        fusedList.sort((d1, d2) ->
                Double.compare(scoreMap.get(d2.getId()), scoreMap.get(d1.getId()))
        );

        return fusedList;
    }
}
