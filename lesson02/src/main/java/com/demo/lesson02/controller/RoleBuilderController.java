package com.demo.lesson02.controller;

import io.micrometer.common.util.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class RoleBuilderController {

    private ChatClient chatClient;

    public RoleBuilderController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一个精通Java的工程师，专门解决Java遇到的问题。") // 设定默认角色
                .defaultUser("你是谁？") // 设置默认问题
                .build();
    }

    @GetMapping(value="/ai/roleBuilder", produces = "text/html;charset=UTF-8")
    public Flux<String> roleBuilder(@RequestParam(value = "message", required = false) String message) {
        if(StringUtils.isNotBlank(message)){
            return this.chatClient.prompt()
                    .user(message)
                    .stream().content();
        }else{
            return this.chatClient.prompt()
                    .stream().content();
        }
    }
}
