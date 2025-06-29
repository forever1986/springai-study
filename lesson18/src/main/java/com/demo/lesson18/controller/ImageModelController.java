package com.demo.lesson18.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@RestController
public class ImageModelController {

    private final ZhiPuAiImageModel imageModel;

    @Autowired
    public ImageModelController(ZhiPuAiImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping("/ai/imagegenerate")
    public void generate(@RequestParam(value = "message", defaultValue = "生成一只老虎！") String message, HttpServletResponse response) throws IOException {
        ImageResponse image = this.imageModel.call(
                new ImagePrompt(new ImageMessage(message)
                        // ZhiPuAiImageOptions参数可以设置模型、图片数量、图片大小等信息，这里必须是图像模型
                        , ZhiPuAiImageOptions.builder().model("cogview-3-flash").build()
                ));
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
