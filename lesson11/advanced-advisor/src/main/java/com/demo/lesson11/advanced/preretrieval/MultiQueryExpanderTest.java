package com.demo.lesson11.advanced.preretrieval;

import com.demo.lesson11.advanced.utils.BuildModelUtils;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;

import java.util.List;

public class MultiQueryExpanderTest {

    public static void main(String[] args) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder()) // 需要引入大模型
                .numberOfQueries(4) // 生成新的问题条数
                .includeOriginal(true) // 是否包括原先问题，默认是true
                .build();
        List<Query> queries = queryExpander.expand(new Query("如何运行一个 Spring Boot 应用程序？"));
        for(Query query : queries){
            System.out.println(query.text());
        }
    }
}
