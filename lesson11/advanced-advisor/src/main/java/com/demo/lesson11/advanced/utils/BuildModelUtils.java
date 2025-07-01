package com.demo.lesson11.advanced.utils;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;

public class BuildModelUtils {

    public static ChatClient.Builder getBuilder(){
        var zhiPuAiApi = new ZhiPuAiApi("720a5f832f4b4754ba4003cde0f92598.4BJ5wgObIG0CFdB5");
        var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model("GLM-4-Flash-250414")
                .temperature(0.4)
                .build());
        return ChatClient.builder(chatModel);
    }
}
