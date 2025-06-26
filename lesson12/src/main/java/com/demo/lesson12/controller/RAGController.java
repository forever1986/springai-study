package com.demo.lesson12.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RAGController {

    private ChatClient chatClient;

    public RAGController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                // 通过Advisors方式，对向量数据库进行封装
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
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