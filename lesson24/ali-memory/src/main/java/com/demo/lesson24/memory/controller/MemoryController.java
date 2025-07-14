package com.demo.lesson24.memory.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class MemoryController {

    private final ChatClient dashScopeChatClient;

    public MemoryController(ChatClient.Builder chatClientBuilder, ChatMemoryRepository chatMemoryRepository) {
        this.dashScopeChatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build())
                        .build()) // 通过不同角色Message方式传递聊天记忆
                .build();
    }

    @GetMapping(value = "/ai/memory", produces = "text/html;charset=UTF-8")
    public Flux<String> generate(@RequestParam(value = "message", required = true) String message
            , @RequestParam(value = "conversationId", required = true) Integer conversationId) {
        return this.dashScopeChatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

}
