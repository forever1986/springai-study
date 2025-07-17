package com.demo.lesson13.transformers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;

import java.util.List;
import java.util.Map;

public class KeywordMetadataEnricherTest {

    public static void main(String[] args) {

        // 创建模型，直接使用ChatModel,而非ChatClient；关于ChatModel后续章节会细讲，这里就是创建一个大语言模型
        var zhiPuAiApi = new ZhiPuAiApi("720a5f832f4b4754ba4003cde0f92598.4BJ5wgObIG0CFdB5");
        var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model("GLM-4-Flash-250414")
                .temperature(0.7)
                .build());

        // 初始化KeywordMetadataEnricher
        KeywordMetadataEnricher enricher = new ZhKeywordMetadataEnricher(chatModel, 5); // 总结5个关键字

        // 初始化文档
        Document doc = new Document("""
                据中国国家海洋环境预报中心官网消息，当地时间7月16日中午时分，美国阿拉斯加半岛海域发生7.3级地震，震源深度为20千米。
                自然资源部海啸预警中心根据最新监测分析结果，此次地震已在震源附近引发局地海啸，不会对我国沿岸造成影响。
                地震发生后，美国国家气象局针对阿拉斯加州南部、阿拉斯加半岛以及临近的太平洋沿岸地区发布了海啸预警，但在一个小时后取消。
                当地警方表示，位于海啸警报范围内的城市都听到了警报声，指示人们转移到高处去。警察局还在社交媒体上发布消息，告知居民，如有需要，当地学校将作为紧急避难所开放。
                数千民众通过警报声和短信通知收到了警报，并按照指示转移到高处或内陆地区避难。沿海地区的渔业、旅游业等经济活动临时暂停。截至发稿，当地还没有出现关于重大损失的报告。
        """);

        // 执行获取关键字
        List<Document> enrichedDocs = enricher.apply(List.of(doc));

        // 打印
        Document enrichedDoc = enrichedDocs.get(0);
        String keywords = (String) enrichedDoc.getMetadata().get("excerpt_keywords");
        System.out.println("提前的关键字: " + keywords);
    }


    /**
     * 由于KeywordMetadataEnricher的默认提示语是英文，因此得到的答案有可能也是英文，因此这里重写一下KeywordMetadataEnricher的提示语
     */
    static class ZhKeywordMetadataEnricher extends KeywordMetadataEnricher{

        private final ChatModel chatModel;

        private final int keywordCount;

        public static final String KEYWORDS_TEMPLATE = """
                {context_str}. 为这份文档提供%s个独特的关键词。格式为以逗号分隔的列表。关键词：""";

        public ZhKeywordMetadataEnricher(ChatModel chatModel, int keywordCount) {
            super(chatModel, keywordCount);
            this.chatModel = chatModel;
            this.keywordCount = keywordCount;
        }

        @Override
        public List<Document> apply(List<Document> documents) {
            for (Document document : documents) {
                var template = new PromptTemplate(String.format(KEYWORDS_TEMPLATE, this.keywordCount));
                Prompt prompt = template.create(Map.of(CONTEXT_STR_PLACEHOLDER, document.getText()));
                String keywords = this.chatModel.call(prompt).getResult().getOutput().getText();
                document.getMetadata().putAll(Map.of("excerpt_keywords", keywords));
            }
            return documents;
        }
    }
}
