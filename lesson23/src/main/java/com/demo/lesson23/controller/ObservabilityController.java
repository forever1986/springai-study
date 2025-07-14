package com.demo.lesson23.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class ObservabilityController {

    private ChatClient chatClient;

    public ObservabilityController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/functionToolCallback")
    public String functionToolCallback(@RequestParam(value = "message",defaultValue = "请问洛杉矶现在几点？", required = true) String message) {
        ToolCallback toolCallback = FunctionToolCallback
                .builder("currentDateTime", new DateTimeTools())
                .description("根据传入国家或地区的名字，获取所在国家或地区时区的当前日期和时间，入参zone为国家或地区的中文名字")
                .inputType(DateTimeRequest.class)
                .build();
        return this.chatClient.prompt()
                .user(message)
                .toolCallbacks(toolCallback) // 添加工具
                .call()
                .content();
    }

    /**
     * 定义一个工具类
     */
    public static class DateTimeTools implements Function<DateTimeRequest, String> {
        @Override
        public String apply(DateTimeRequest zone) {
            Map<String,String> map = new HashMap<>();
            map.put("北京","Asia/Shanghai");
            map.put("伦敦","Europe/London");
            map.put("洛杉矶","America/Los_Angeles");
            ZoneId zoneIdGMT = ZoneId.of(map.get(zone.location())==null?"Asia/Shanghai":map.get(zone.location()));
            String time = LocalDateTime.now(zoneIdGMT).toString();
            return time;
        }
    }

    // 看看3.2.3的参数限制，不支持原始数据类型，因此需要自定义一个POJO
    public record DateTimeRequest(String location) {}

}