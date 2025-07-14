package com.demo.lesson24.prompt.controller;

import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplate;
import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplateFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class DemoController {

    private final ChatClient dashScopeChatClient;
    private final ConfigurablePromptTemplateFactory promptTemplateFactory;


    public DemoController(ChatClient.Builder chatClientBuilder, ConfigurablePromptTemplateFactory promptTemplateFactory) {
        this.dashScopeChatClient = chatClientBuilder.build();
        this.promptTemplateFactory = promptTemplateFactory;
    }

    @GetMapping(value = "/ai/generate", produces = "text/html;charset=UTF-8")
    public Flux<String> generate(@RequestParam(value = "authorName", required = false, defaultValue = "") String authorName) {
        // 使用 nacos 的 prompt tmpl 创建 prompt
        ConfigurablePromptTemplate template = promptTemplateFactory.create(
                // 配置的模版name
                "author",
                // 如果找不到，会使用这个默认模版
                "请列出这位{author}最著名的三本书。"
        );
        Prompt prompt = null;
        if(!StringUtils.hasText(authorName)){
            // 但参数为空时，使用nacos默认的author关键字
            prompt = template.create();
        }else{
            prompt = template.create(Map.of("author", authorName));
        }
        return dashScopeChatClient.prompt(prompt).stream().content();
    }

}
