package com.demo.lesson11.advanced.preretrieval;

import com.demo.lesson11.advanced.utils.BuildModelUtils;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;

public class RewriteQueryTransformerTest {

    public static void main(String[] args) {
        Query query = new Query("我正在学习机器学习相关课程，LLM是什么？");

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder()) // 需要引入大模型
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery.text());
    }

}
