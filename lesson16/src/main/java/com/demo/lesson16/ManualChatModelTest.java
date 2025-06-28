package com.demo.lesson16;

import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;

public class ManualChatModelTest {

    public static void main(String[] args) {
        // 设置API KEY
        var zhiPuAiApi = new ZhiPuAiApi("720a5f832f4b4754ba4003cde0f92598.4BJ5wgObIG0CFdB5");
        var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                // 设置模型
                .model("GLM-4-Flash-250414")
                // 设置温度
                .temperature(0.7)
                .build());
        System.out.println(chatModel.call("请跟我说个笑话！"));
    }
}
