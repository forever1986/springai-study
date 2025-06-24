package com.demo.lesson07.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionBeanController {

    private ChatClient chatClient;

    public FunctionBeanController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/functionBean")
    public String functionBean(@RequestParam(value = "message", required = true) String message) {
        return this.chatClient.prompt()
                .user(message)
                .toolNames("dateTimeTools") // 添加工具
                .call()
                .content();
    }

}