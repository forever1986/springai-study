package com.demo.lesson11.advanced.retrieval;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcatenationDocumentJoinerTest {
    public static void main(String[] args) {
        // 模拟多查询后的结果
        Map<Query, List<List<Document>>> documentsForQuery = getDocumentsMap();
        // 使用ConcatenationDocumentJoiner合并
        DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();
        List<Document> documents = documentJoiner.join(documentsForQuery);
        // 打印
        int i = 0;
        for (Document document: documents){
            System.out.println("-----" + (++i) + "-----");
            System.out.println(document.getText());
        }
    }

    private static Map<Query, List<List<Document>>> getDocumentsMap(){

        Document document1 = new Document("""
                ChatGLM3 是北京智谱华章科技有限公司和清华大学 KEG 实验室联合发布的对话预训练模型。ChatGLM3-6B 是 ChatGLM3 系列中的开源模型，在保留了前两代模型对话流畅、部署门槛低等众多优秀特性的基础上，ChatGLM3-6B 引入了更多的特性。\n
                北京智谱华章科技有限公司是一家来自中国的公司，致力于打造新一代认知智能大模型，专注于做大模型创新。
                """);
        document1.getMetadata().put("source", "官方网站");

        Document document2 = new Document("""
                OpenAI，是一家开放人工智能研究和部署公司，其使命是确保通用人工智能造福全人类。创立于2015年12月，总部位于美国旧金山。现由营利性公司OpenAI LP及非营利性母公司OpenAI Inc组成。
                """);
        document2.getMetadata().put("source", "官方网站");

        Document document3 = new Document("""
                ChatGLM3是由智谱清言开发的，是北京智谱华章科技有限公司推出的生成式AI助手，于2023年8月31日正式上线。 2024年8月29日，智谱清言APP支持视频通话功能。
                智谱清言基于智谱AI自主研发的中英双语对话模型还开发了ChatGLM、ChatGLM2、ChatGLM3、ChatGLM4等大模型。
                """);
        document3.getMetadata().put("source", "官方网站");

        Document document4 = new Document("""
                ChatGLM3 是由清华大学技术成果转化企业智谱AI研发的支持中英双语的对话机器人，基于千亿参数基座模型GLM架构开发。该模型通过多阶段训练流程形成通用对话能力，具备问答交互、代码生成、创意写作等功能，其开源版本ChatGLM-6B自2023年3月启动内测以来已形成广泛影响力。\n
                截至2024年3月，智谱AI通过该技术实现了2000多家生态合作伙伴的应用落地，并在多模态技术上持续突破，推出了视频生成等创新功能。清华大学是中国排名top2的学校。
                """);
        document4.getMetadata().put("source", "百度百科");

        Query query1 =Query.builder()
                .text("ChatGLM3是哪个国家的大模型？")
                .build();
        List<Document> list1 = new ArrayList<>();
        list1.add(document1);
        list1.add(document2);
        Query query2 =Query.builder()
                .text("ChatGLM3 的研发国家是哪个？")
                .build();
        List<Document> list2 = new ArrayList<>();
        list2.add(document1);
        list2.add(document3);

        Map<Query, List<List<Document>>> map = new HashMap<>();
        map.put(query1,List.of(list1));
        map.put(query2,List.of(list2));
        return map;
    }
}
