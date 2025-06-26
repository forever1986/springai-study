package com.demo.lesson14.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChromaVectorStoreController {

    private ChatClient chatClient;

    public ChromaVectorStoreController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        List<Document> documents = List.of(new Document("ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了如下特性：\n" +
                "\n" +
                "更强大的基础模型： ChatGLM3-6B 的基础模型 ChatGLM3-6B-Base 采用了更多样的训练数据、更充分的训练步数和更合理的训练策略。在语义、数学、推理、代码、知识等不同角度的数据集上测评显示，* ChatGLM3-6B-Base 具有在 10B 以下的基础模型中最强的性能*。\n" +
                "更完整的功能支持： ChatGLM3-6B 采用了全新设计的 Prompt 格式 ，除正常的多轮对话外。同时原生支持工具调用（Function Call）、代码执行（Code Interpreter）和 Agent 任务等复杂场景。\n" +
                "更全面的开源序列： 除了对话模型 ChatGLM3-6B 外，还开源了基础模型 ChatGLM3-6B-Base 、长文本对话模型 ChatGLM3-6B-32K 和进一步强化了对于长文本理解能力的 ChatGLM3-6B-128K。以上所有权重对学术研究完全开放 ，在填写 问卷 进行登记后亦允许免费商业使用。\n" +
                "\n" +
                "北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。"));
        vectorStore.add(documents);
        this.chatClient = chatClientBuilder
                // 通过Advisors方式，对向量数据库进行封装
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/chroma")
    public String rag(@RequestParam(value = "message", required = true, defaultValue = "ChatGLM3是哪个国家的大模型？") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
