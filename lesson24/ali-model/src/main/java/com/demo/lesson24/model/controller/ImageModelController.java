package com.demo.lesson24.model.controller;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

@RestController
public class ImageModelController {

    private final DashScopeImageModel dashScopeImageModel;

    @Autowired
    public ImageModelController(DashScopeImageModel dashScopeImageModel) {
        this.dashScopeImageModel = dashScopeImageModel;
    }

    @GetMapping("/ai/imagegenerate")
    public void generate(@RequestParam(value = "message", defaultValue = "生成一只老虎！") String message, HttpServletResponse response) throws IOException {
        ImageResponse image = this.dashScopeImageModel.call(
                new ImagePrompt(new ImageMessage(message), DashScopeImageOptions.builder().build()));
        // 返回的URL
        String url = image.getResult().getOutput().getUrl();
        // 将URL转为Stream输出到HttpServletResponse
        URL imageURL = URI.create(url).toURL();
        InputStream inputStream = imageURL.openStream();
        response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
        response.getOutputStream().write(inputStream.readAllBytes());
        response.getOutputStream().flush();
    }

}
