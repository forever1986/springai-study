package com.demo.lesson24.model.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeSpeechSynthesisApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisMessage;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class AudioSpeechController {

    private final DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel;

    @Autowired
    public AudioSpeechController(DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel) {
        this.dashScopeSpeechSynthesisModel = dashScopeSpeechSynthesisModel;
    }

    @GetMapping("/ai/audio")
    public void generate(@RequestParam(value = "message", defaultValue = "你怎么这样凭空污人清白……窃书不能算偷……窃书！……读书人的事，能算偷么？") String message) {
        DashScopeSpeechSynthesisOptions speechOptions = DashScopeSpeechSynthesisOptions.builder()
                .model("cosyvoice-v2") // 使用扣子模型
                .voice("longtao_v2") // 使用粤语语音
                .responseFormat(DashScopeSpeechSynthesisApi.ResponseFormat.WAV) // WAV格式
                .speed(1.0f) // 正常语速
                .build();

        SpeechSynthesisPrompt speechPrompt = new SpeechSynthesisPrompt(new SpeechSynthesisMessage(message), speechOptions);
        SpeechSynthesisResponse speechResponse = dashScopeSpeechSynthesisModel.call(speechPrompt);
        File file = new File("lesson24/ali-model/ali-output.wav");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(speechResponse.getResult().getOutput().getAudio().array());
            fileOutputStream.flush();
        }catch (IOException e){
            // do something
        }
    }
}
