package com.demo.lesson10.stdio.server;

import com.demo.lesson10.stdio.server.service.WeatherService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Lesson10MCPServerStdioApplication {

    @Bean
    public List<ToolCallback> weatherTools(WeatherService weatherService) {
        return Arrays.stream(ToolCallbacks.from(weatherService)).toList();
    }

    public static void main(String[] args) {
        SpringApplication.run(Lesson10MCPServerStdioApplication.class, args);
    }
}
