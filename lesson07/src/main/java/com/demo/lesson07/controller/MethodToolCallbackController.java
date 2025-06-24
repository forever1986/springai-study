package com.demo.lesson07.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MethodToolCallbackController {

    private ChatClient chatClient;

    public MethodToolCallbackController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/methodToolCallback")
    public String methodToolCallback(@RequestParam(value = "message", required = true) String message) {
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime", String.class);
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .name("getCurrentDateTime")
                        .description("根据传入国家或地区的名字，获取所在国家或地区时区的当前日期和时间，入参zone为国家或地区的名字")
                        .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                        .build())
                .toolMethod(method)
                .toolObject(new DateTimeTools())
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
    static class DateTimeTools {

        public String getCurrentDateTime(String zone) {
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