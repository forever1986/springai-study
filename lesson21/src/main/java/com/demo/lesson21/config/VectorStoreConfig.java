package com.demo.lesson21.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class VectorStoreConfig {

    /**
     * @param embeddingModel - 这里如果没有特殊配置，默认使用的是你引入model的embedding模型
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        simpleVectorStore.add(getDocuments());
        return simpleVectorStore;
    }

    private static List<Document> getDocuments(){
        Document document1 = new Document("""
                ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了更多的特性。\n
                北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。
                """);
        document1.getMetadata().put("source", "官方网站");

        List<Document> list = new ArrayList<>();
        list.add(document1);
        return list;
    }
}
