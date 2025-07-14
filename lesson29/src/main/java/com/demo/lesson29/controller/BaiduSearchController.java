package com.demo.lesson29.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaiduSearchController {

    private ChatClient chatClient;

    public BaiduSearchController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultToolNames("baiduSearch")
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/baidu")
    public String baidu(@RequestParam(value = "message", defaultValue = "从百度搜索中查找北京天气？", required = true) String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
