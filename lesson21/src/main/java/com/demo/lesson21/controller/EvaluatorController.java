package com.demo.lesson21.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EvaluatorController {

    private final ChatClient.Builder builder;
    private final VectorStore vectorStore;

    public EvaluatorController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.builder = builder;
        this.vectorStore = vectorStore;
    }

    /**
     * RelevancyEvaluator示例演示
     */
    @GetMapping("/ai/relevancyEvaluator")
    public Boolean relevancyEvaluator() {
        // 问题
        String question = "ChatGLM3是哪个国家的大模型？";

        // RAG
        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
//                        .vectorStore(vectorStore)
                        .build())
                .build();
        ChatResponse chatResponse = this.builder.build()
                .prompt(question)
                .advisors(ragAdvisor)
                .call()
                .chatResponse();

        // 评估
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // 原始问题
                question,
                // 提供给RAG的文档
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // RAG的回复
                chatResponse.getResult().getOutput().getText()
        );

        // 重新定义提示词
        PromptTemplate promptTemplate = new PromptTemplate("""
				您的任务是评估该问题的回复是否与所提供的上下文具有较大一致性和相关性。
				您的最终回答有两个选项可供选择。分别是“是”或“否”。
                如果对于该问题的回答与上下文相符，则回答“yes”；否则回答“no”。
                问题：{query}
                回复：{response}
                上下文：{context}
                回答：
                """);

        // 评估
        RelevancyEvaluator evaluator = RelevancyEvaluator.builder()
                .chatClientBuilder(builder)
                .promptTemplate(promptTemplate)
                .build();
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        // 返回结果
        return evaluationResponse.isPass();
    }

    /**
     * FactCheckingEvaluator 示例
     */
    @GetMapping("/ai/factCheckingEvaluator")
    public Boolean factCheckingEvaluator() {

        Document document1 = new Document("""
                ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了更多的特性。\n
                北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。
                """);
        // 评估
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // 原始问题
                "ChatGLM3是哪个国家的大模型？",
                // 提供文档
                List.of(document1),
                // 主张
                "ChatGLM3是美国的大模型"
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
