package com.demo.lesson02.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class RolePromptController {

    private ChatClient chatClient;

    public RolePromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping(value="/ai/rolePrompt", produces = "text/html;charset=UTF-8")
    public Flux<String> rolePrompt(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        Message systemMessage = new SystemMessage("你是一个精通Java的工程师，专门解决Java遇到的问题。");
        Message userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
        return this.chatClient.prompt(prompt)
                .stream().content();
    }
}