package com.demo.lesson15.controller;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatModelRoleController {

    private final ChatModel chatModel;

    @Autowired
    public ChatModelRoleController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping(value = "/chatmodel/systemrole", produces = "text/html;charset=UTF-8") //设定返回的字符，不然会出现乱码
    public String systemrole(@RequestParam(value = "message", defaultValue = "空指针一般有什么问题造成的？") String message) {
        Message systemMessage = new SystemMessage("你是一个精通Java的工程师，专门解决Java遇到的问题。");
        Message userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
        return this.chatModel.call(prompt).getResult().getOutput().getText();
    }
}
