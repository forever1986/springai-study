package com.demo.lesson13.transformers;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher.SummaryType;
import java.util.List;

public class SummaryMetadataEnricherTest {

    public static void main(String[] args) {

        // 创建模型，直接使用ChatModel,而非ChatClient；关于ChatModel后续章节会细讲，这里就是创建一个大语言模型
        var zhiPuAiApi = new ZhiPuAiApi("720a5f832f4b4754ba4003cde0f92598.4BJ5wgObIG0CFdB5");
        var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model("GLM-4-Flash-250414")
                .temperature(0.7)
                .build());

        // 初始化SummaryMetadataEnricher
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryType.PREVIOUS, SummaryType.CURRENT, SummaryType.NEXT),
                """
                以下是该部分的内容：
                {context_str}
                概述该部分的主要主题和相关实体。
                摘要：""", //提示词改为中文的
                MetadataMode.ALL);

        // 初始化文档
        Document doc1 = new Document("""
                今年是中国人民抗日战争暨世界反法西斯战争胜利80周年，党和国家将隆重举行纪念活动。
                今天上午，国新办举行新闻发布会，介绍了纪念活动总体安排。主要的活动安排，共有十项。总书记将出席纪念中国人民抗日战争暨世界反法西斯战争胜利80周年大会并发表重要讲话。""");
        Document doc2 = new Document("""
                阅兵是纪念活动的重要组成部分，主题是纪念抗战伟大胜利、弘扬抗战伟大精神。相信大家一定对这次阅兵的参阅装备非常关注。据介绍，受阅的武器装备全部为国产现役主战装备，是我军体系作战能力、新域新质战力、战略威慑实力的集中展示，是我国武器装备自主创新能力的集中体现。
                届时，将向全国人民和全世界人民奉献一场弘扬抗战精神、体现时代特色、具有大国气派的阅兵盛典。
                这次纪念活动以党和国家名义举行的活动就有5项，包括纪念大会、招待会、颁发纪念章、主题展览开幕式、国家公祭仪式等。之所以安排这么多高规格的活动，就是要展现党和国家对历史的尊重、对英烈的尊崇，更好厚植和激发人们的爱国情怀。
                值得一提的是，党中央明确要求，各项纪念活动确保既隆重热烈又务实简朴。""");

        // 执行摘要总结
        List<Document> enrichedDocs = enricher.apply(List.of(doc1,doc2));

        // 打印
        int i = 0;
        for (Document doc : enrichedDocs) {
            System.out.println("--------"+(++i)+"--------");
            System.out.println("当前文档总结: " + doc.getMetadata().get("section_summary"));
            System.out.println("前一份文档总结: " + doc.getMetadata().get("prev_section_summary"));
            System.out.println("下一份文档总结: " + doc.getMetadata().get("next_section_summary"));
        }
    }

}
