package com.demo.lesson19.controller;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class AudioController {

    /**
     * 需要将application.properties里面的url和apikey改为openai
     */
    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    @Autowired
    public AudioController(OpenAiAudioSpeechModel openAiAudioSpeechModel) {
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
    }

    @GetMapping("/ai/audio")
    public void generate(@RequestParam(value = "message", defaultValue = "你怎么这样凭空污人清白……窃书不能算偷……窃书！……读书人的事，能算偷么？") String message) {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .model("tts-1")
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(message, speechOptions);
        SpeechResponse speechResponse = openAiAudioSpeechModel.call(speechPrompt);
        File file = new File("output.wav");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(speechResponse.getResult().getOutput());
            fileOutputStream.flush();
        }catch (IOException e){
            // do something
        }
    }
}
