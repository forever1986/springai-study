package com.demo.lesson12.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class VectorStoreConfig {

    /**
     * @param transformersEmbeddingModel - 这里使用TransformersEmbeddingModel，就是配置加载本地的embedding模型
     */
    @Bean
    public VectorStore vectorStore(TransformersEmbeddingModel transformersEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(transformersEmbeddingModel).build();
        List<Document> documents = List.of(new Document("ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了如下特性：\n" +
                "\n" +
                "更强大的基础模型： ChatGLM3-6B 的基础模型 ChatGLM3-6B-Base 采用了更多样的训练数据、更充分的训练步数和更合理的训练策略。在语义、数学、推理、代码、知识等不同角度的数据集上测评显示，* ChatGLM3-6B-Base 具有在 10B 以下的基础模型中最强的性能*。\n" +
                "更完整的功能支持： ChatGLM3-6B 采用了全新设计的 Prompt 格式 ，除正常的多轮对话外。同时原生支持工具调用（Function Call）、代码执行（Code Interpreter）和 Agent 任务等复杂场景。\n" +
                "更全面的开源序列： 除了对话模型 ChatGLM3-6B 外，还开源了基础模型 ChatGLM3-6B-Base 、长文本对话模型 ChatGLM3-6B-32K 和进一步强化了对于长文本理解能力的 ChatGLM3-6B-128K。以上所有权重对学术研究完全开放 ，在填写 问卷 进行登记后亦允许免费商业使用。\n" +
                "\n" +
                "北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。"));
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
