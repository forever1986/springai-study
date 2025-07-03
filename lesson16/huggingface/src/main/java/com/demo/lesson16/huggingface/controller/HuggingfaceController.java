package com.demo.lesson16.huggingface.controller;

import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HuggingfaceController {

    private final HuggingfaceChatModel chatModel;

    @Autowired
    public HuggingfaceController(HuggingfaceChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        return this.chatModel.call(message);
    }

}
