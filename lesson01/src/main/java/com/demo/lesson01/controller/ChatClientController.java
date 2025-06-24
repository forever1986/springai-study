package com.demo.lesson01.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatClientController {

    private ChatClient chatClient;

    public ChatClientController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁！") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call().content();
    }

    @GetMapping(value="/ai/generateStream", produces = "text/html;charset=UTF-8") //设定返回的字符，不然会出现乱码
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁！") String message) {
        return this.chatClient.prompt()
                .user(message)
                .stream().content();
    }
}
