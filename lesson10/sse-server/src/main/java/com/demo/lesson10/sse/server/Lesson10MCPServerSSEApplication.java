package com.demo.lesson10.sse.server;

import com.demo.lesson10.sse.server.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lesson10MCPServerSSEApplication {

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Lesson10MCPServerSSEApplication.class, args);
    }
}
