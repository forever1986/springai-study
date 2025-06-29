package com.demo.lesson19.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioTranscriptionController {

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    @Autowired
    public AudioTranscriptionController(OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel) {
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
    }

    @GetMapping("/ai/audioTranscription")
    public String generate() {

        // 返回翻译之后的格式，VTT是一种字幕文件格式，你可以选择其它的，比如json、text、srt等
        OpenAiAudioApi.TranscriptResponseFormat responseFormat = OpenAiAudioApi.TranscriptResponseFormat.VTT;

        // 配置语音的属性，包括语言、提示文字、温度等
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .model("glm-asr")
                .language("中文")
                .prompt("请将语音附件访问为文字。")
                .temperature(0f)
                .responseFormat(responseFormat)
                .build();
        // 语音文件
        Resource resource = new ClassPathResource("data/output.wav");
        // 组织提示请求参数
        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(resource, transcriptionOptions);
        // 转文字
        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(transcriptionRequest);

        return response.getResult().getOutput();
    }
}
