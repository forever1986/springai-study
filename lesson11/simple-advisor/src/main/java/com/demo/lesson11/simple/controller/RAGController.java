package com.demo.lesson11.simple.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RAGController {

    private ChatClient chatClient;

    public RAGController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template("""
                        {query}
                        参考信息如下，使用“---------------------”标识包裹在里面的为参考信息。
                        
                        ---------------------
                        {question_answer_context}
                        ---------------------
                        
                        鉴于当前的参考信息以及所提供的历史信息（而非任何先入为主的了解），请回复用户评论。如果答案不在上述背景信息中，告知用户您无法回答该问题。
                        """)
                .build();
        var b = new FilterExpressionBuilder().eq("source", "官方网站").build();
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(promptTemplate)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.2) //相似度阈值，只有大于等于该值才会被返回（取值范围:0-1），默认是0（没有相似度排除）
                        .topK(2) // 返回相似度排名前2的文档，默认是4
                        .filterExpression(b) // 通过文档的元数据过滤
                        .build())
                .build();
        this.chatClient = chatClientBuilder
                // 通过Advisors方式，对向量数据库进行封装
                .defaultAdvisors(questionAnswerAdvisor)
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/rag")
    public String rag(@RequestParam(value = "message", required = true, defaultValue = "ChatGLM3是哪个国家的大模型？") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}