package com.demo.lesson04.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryController {

    private ChatClient chatClient;

    public MemoryController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                // 通过Advisors设置聊天记忆模式，关于Advisors后续会细讲
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) // 通过不同角色Message方式传递聊天记忆
//                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build()) // 通过提示词的方式传递聊天记忆
                .build();
    }

    /**
     * @param message 问题
     * @param conversationId 聊天记忆的id
     */
    @GetMapping("/ai/memory")
    public String memory(@RequestParam(value = "message", required = true) String message
            , @RequestParam(value = "conversationId", required = true) Integer conversationId) {
        return this.chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

}