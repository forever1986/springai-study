package com.demo.lesson11.advanced.generation;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;

import java.util.ArrayList;
import java.util.List;

public class ContextualQueryAugmenterTest {
    public static void main(String[] args) {
        Document document1 = new Document("""
                ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了更多的特性。\n
                北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。
                """);
        document1.getMetadata().put("source", "官方网站");

        Document document2 = new Document("""
                OpenAI，是一家开放人工智能研究和部署公司，其使命是确保通用人工智能造福全人类。创立于2015年12月，总部位于美国旧金山。现由营利性公司OpenAI LP及非营利性母公司OpenAI Inc组成。
                """);
        document2.getMetadata().put("source", "官方网站");

        List<Document> list = new ArrayList<>();
        list.add(document1);
        list.add(document2);

        PromptTemplate promptTemplate = new PromptTemplate("""
                以下为相关背景信息。
                ---------------------
                {context}
                ---------------------
                
                根据提供的背景信息且没有先入为主的观念，回答问题。
                请遵循以下规则：
                1. 如果答案不在所提供的信息中，那就直接说你不知道。
                2. 避免使用诸如“根据上下文……”或“所提供的信息……”这样的表述。
                查询：{query}
                回答：
                """);
        QueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder().promptTemplate(promptTemplate).build();
        Query query =Query.builder()
                .text("ChatGLM3是哪个国家的大模型？")
                .build();
        System.out.println(queryAugmenter.augment(query, list).text());

    }
}
