package com.demo.lesson17.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        // 自动注入智谱的EmbeddingModel模型
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/ai/embeded")
    public float[] embeded(@RequestParam(value = "message", required = true, defaultValue = "测试进行嵌入") String message) {
        float[] embed = embeddingModel.embed(message);
        System.out.println("输出的嵌入向量维度："+embed.length);
        return embed;
    }
}
