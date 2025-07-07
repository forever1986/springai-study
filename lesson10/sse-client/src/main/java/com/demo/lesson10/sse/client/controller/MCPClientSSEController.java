package com.demo.lesson10.sse.client.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MCPClientSSEController {

    private ChatClient chatClient;

    public MCPClientSSEController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(tools)
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/sse/client")
    public String amapMaps(@RequestParam(value = "message", required = true) String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}