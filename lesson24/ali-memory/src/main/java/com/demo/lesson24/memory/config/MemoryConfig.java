package com.demo.lesson24.memory.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

    @Value("${spring.ai.memory.redis.host}")
    private String redisHost;
    @Value("${spring.ai.memory.redis.port}")
    private int redisPort;
    // 若没有设置密码则注释该项
//    @Value("${spring.ai.memory.redis.password}")
//    private String redisPassword;
    @Value("${spring.ai.memory.redis.timeout}")
    private int redisTimeout;

    @Bean
    public RedisChatMemoryRepository chatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(redisHost)
                .port(redisPort)
                // 若没有设置密码则注释该项
//				.password(redisPassword)
                .timeout(redisTimeout)
                .build();
    }
}
