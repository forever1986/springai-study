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
        Document doc = new Document("9月3日，这是一个值得永远纪念的日子。举行纪念活动，正是为了铭记历史、缅怀先烈，弘扬伟大爱国主义精神、伟大抗战精神。咱们共同关注、一起期待，以更好汲取前行的智慧和力量。");

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
