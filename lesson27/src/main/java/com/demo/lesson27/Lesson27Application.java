package com.demo.lesson27;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 注意：这里加载com.alibaba.cloud.ai包下，是因为Spring AI Alibaba-nl2sql并不是自动配置，因此需要扫描其路径，同时也要扫描自己路径下的包
@SpringBootApplication(scanBasePackages = {"com.alibaba.cloud.ai","com.demo.lesson27"})
public class Lesson27Application {

    public static void main(String[] args) {
        SpringApplication.run(Lesson27Application.class, args);
    }

}
