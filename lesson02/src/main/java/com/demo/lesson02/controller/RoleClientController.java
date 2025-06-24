package com.demo.lesson02.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class RoleClientController {

    private ChatClient chatClient;

    public RoleClientController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping(value="/ai/roleClient", produces = "text/html;charset=UTF-8")
    public Flux<String> roleClient(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        return this.chatClient.prompt()
                .system("你是一个精通Java的工程师，专门解决Java遇到的问题。") // 设定默认角色
                .user(message)
                .stream().content();
    }
}
