package com.demo.lesson03.controller;

import com.demo.lesson03.dto.ActorsFilms;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StructuredOutputController {

    private ChatClient chatClient;

    public StructuredOutputController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    // 自动转换为java bean对象
    @GetMapping("/ai/beanStructuredOutput")
    public ActorsFilms beanStructuredOutput() {
        ActorsFilms actorsFilms = this.chatClient.prompt()
                .user(u -> u.text("生成{actor}的5部电影作品列表.")
                        .param("actor", "周星驰"))
                .call()
                .entity(ActorsFilms.class);
        return actorsFilms;
    }

    // 自动转换为List对象
    @GetMapping("/ai/listStructuredOutput")
    public List<String> listStructuredOutput() {
        List<String> flavors = this.chatClient.prompt()
                .user(u -> u.text("列出5种{subject}")
                        .param("subject", "冰淇淋口味"))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
        return flavors;
    }

    // 自动转换为Map对象
    @GetMapping("/ai/mapStructuredOutput")
    public Map<String, Object> mapStructuredOutput() {
        Map<String, Object> result = this.chatClient.prompt()
                .user(u -> u.text("给我提供一份关于{subject}的清单")
                        .param("subject", "在键名为“numbers”的数组中包含从 1 到 9 的一系列数字"))
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
        return result;
    }

}