package com.demo.lesson07.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MethodToolController {

    private ChatClient chatClient;

    public MethodToolController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/methodTool")
    public String methodTool(@RequestParam(value = "message", required = true) String message) {
        return this.chatClient.prompt()
                .user(message)
                .tools(new DateTimeTools()) // 添加工具
                .call()
                .content();
    }

    /**
     * 定义一个工具类
     */
    static class DateTimeTools {

        // 使用Tool注解进行工具定义
        @Tool(description = "根据传入国家或地区的名字，获取所在国家或地区时区的当前日期和时间")
        public String getCurrentDateTime(@ToolParam(description = "国家或地区的名字") String zone) {
            Map<String,String> map = new HashMap<>();
            map.put("北京","Asia/Shanghai");
            map.put("伦敦","Europe/London");
            map.put("洛杉矶","America/Los_Angeles");
            ZoneId zoneIdGMT = ZoneId.of(map.get(zone)==null?"Asia/Shanghai":map.get(zone));
            String time = LocalDateTime.now(zoneIdGMT).toString();
            System.out.println(time);
            return time;
        }
    }

}