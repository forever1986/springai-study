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
                据中国国家海洋环境预报中心官网消息，当地时间7月16日中午时分，美国阿拉斯加半岛海域发生7.3级地震，震源深度为20千米。
                自然资源部海啸预警中心根据最新监测分析结果，此次地震已在震源附近引发局地海啸，不会对我国沿岸造成影响。""");
        Document doc2 = new Document("""
                当地时间6月12日下午，一架从印度飞往英国的印度航空公司波音787-8型客机在印度古吉拉特邦艾哈迈达巴德机场起飞后不久坠毁，造成机上和地面共270余人遇难。
                飞机上的两个黑匣子均已被找到。这是波音787机型首次发生致命空难，多国已派团队协助调查。""");

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
