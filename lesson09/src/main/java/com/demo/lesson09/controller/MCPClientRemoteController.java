package com.demo.lesson09.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MCPClientRemoteController {

    private ChatClient chatClient;

    public MCPClientRemoteController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(tools)
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/MCPClientRemote")
    public String amapMaps(@RequestParam(value = "message", required = true) String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}