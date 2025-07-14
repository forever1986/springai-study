package com.demo.lesson30.knowledge.controller;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties({DashScopeConnectionProperties.class})
public class BailianKnowledgeController {

    private static final String indexName = "庆余年小说";

    private final ChatClient chatClient;

    public BailianKnowledgeController(ChatClient.Builder builder, DashScopeConnectionProperties properties) {

        DocumentRetriever retriever = new DashScopeDocumentRetriever(DashScopeApi.builder().apiKey(properties.getApiKey()).build(),
                DashScopeDocumentRetrieverOptions.builder().withIndexName(indexName).build());
        chatClient = builder
                .defaultAdvisors(new DocumentRetrievalAdvisor(retriever))
                .build();
    }

    @GetMapping("/bailian/knowledge/call")
    public String call(@RequestParam(value = "message",
            defaultValue = "你是谁?") String message) {

        return chatClient.prompt().user(message).call().content();

    }

}
