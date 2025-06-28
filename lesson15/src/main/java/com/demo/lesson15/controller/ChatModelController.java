package com.demo.lesson15.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatModelController {

    private final ChatModel chatModel;

    @Autowired
    public ChatModelController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chatmodel/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "请跟我说个笑话！") String message) {
        return this.chatModel.call(message);
    }
}
