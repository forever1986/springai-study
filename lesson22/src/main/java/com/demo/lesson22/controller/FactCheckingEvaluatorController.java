package com.demo.lesson22.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FactCheckingEvaluatorController {

    private final ChatClient.Builder builder;

    public FactCheckingEvaluatorController(ChatClient.Builder builder) {
        this.builder = builder;
    }

    /**
     * FactCheckingEvaluator 示例
     */
    @GetMapping("/ai/factCheckingEvaluator")
    public Boolean factCheckingEvaluator(@RequestParam(value = "message", defaultValue = "ChatGLM3是中国的大模型！") String message) {

        Document document1 = new Document("""
                ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了更多的特性。\n
                北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。
                """);
        // 评估
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // 原始问题
                "",
                // 提供文档
                List.of(document1),
                // 主张
                message
        );

        // 重新定义提示词
        PromptTemplate promptTemplate = new PromptTemplate("""
                评估所提供的文档是否支持以下主张。
                如果主张得到支持，请回答“yes”，否则回答“no”。
                文档：\\n {document}\\n
                主张：\\n {claim}
                """);

        // 评估
        FactCheckingEvaluator evaluator = new FactCheckingEvaluator(builder, promptTemplate.getTemplate());
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        // 返回结果
        return evaluationResponse.isPass();
    }

}
