package com.demo.lesson02.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PromptTemplateController {

    private ChatClient chatClient;

    public PromptTemplateController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping(value="/ai/promptTemplate", produces = "text/html;charset=UTF-8")
    public String prompt(@RequestParam(value = "message", defaultValue = "你是谁？") String message, @RequestParam(value = "type", defaultValue = "小众") String type) {

        // 设置模版的样式语句，其中{}是占位符
        String defaultSystemText = "你的名字叫{name}，你是一个{type}风格的旅游博主，专门介绍和推荐{type}风格相关的旅游内容。在每次回答用户的问题之前先说:'你好，我叫{name}'，再回答用户的问题";
        // 创建一个PromptTemplate
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(defaultSystemText);
        // 设置占位符实际内容并返回system角色的message
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name","周星驰","type",type));
        // 设置用户的问题message
        Message userMessage = new UserMessage(message);
        // 组装Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        // 调用结果
        return this.chatClient.prompt(prompt)
                .call().content();
    }
}