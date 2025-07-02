package com.demo.lesson21.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RelevancyEvaluatorController {

    private final ChatClient.Builder builder;
    private final VectorStore vectorStore;

    public RelevancyEvaluatorController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.builder = builder;
        this.vectorStore = vectorStore;
    }

    /**
     * RelevancyEvaluator示例演示
     */
    @GetMapping("/ai/relevancyEvaluator")
    public Boolean relevancyEvaluator() {
        // 问题
        String question = "ChatGLM3是哪个国家的大模型？";

        // RAG
        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
//                        .vectorStore(vectorStore)
                        .build())
                .build();
        ChatResponse chatResponse = this.builder.build()
                .prompt(question)
                .advisors(ragAdvisor)
                .call()
                .chatResponse();

        // 评估
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // 原始问题
                question,
                // 提供给RAG的文档
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // RAG的回复
                chatResponse.getResult().getOutput().getText()
        );

        // 重新定义提示词
        PromptTemplate promptTemplate = new PromptTemplate("""
				您的任务是评估该问题的回复是否与所提供的上下文具有较大一致性和相关性。
				您的最终回答有两个选项可供选择。分别是“是”或“否”。
                如果对于该问题的回答与上下文相符，则回答“yes”；否则回答“no”。
                问题：{query}
                回复：{response}
                上下文：{context}
                回答：
                """);

        // 评估
        RelevancyEvaluator evaluator = RelevancyEvaluator.builder()
                .chatClientBuilder(builder)
                .promptTemplate(promptTemplate)
                .build();
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        // 返回结果
        return evaluationResponse.isPass();
    }

}
