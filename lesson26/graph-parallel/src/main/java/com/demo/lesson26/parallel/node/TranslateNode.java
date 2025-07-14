package com.demo.lesson26.parallel.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

public class TranslateNode implements NodeAction {

    private static final PromptTemplate DEFAULTPROMPTTEMPLATE = new PromptTemplate("""
            对于用户输入的查询，将其翻译成 {targetLanguage}。
            如果查询已经是 {targetLanguage} 的形式，则无需更改，直接返回。
            如果不知道查询的语言，则也无需更改。
            请勿添加解释或任何其他文字。
            原始查询：{query}
            原始查询：
            """);

    private final ChatClient chatClient;

    private final String  TARGETLANGUAGE= "English"; // 默认英语

    public TranslateNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) {
        String query = state.value("query", "");
        String targetLanguage = state.value("translatelanguage", TARGETLANGUAGE);

        Flux<String> streamResult = this.chatClient.prompt().user((user) -> user.text(DEFAULTPROMPTTEMPLATE.getTemplate()).param("targetLanguage", targetLanguage).param("query", query)).stream().content();
        String result = streamResult.reduce("", (acc, item) -> acc + item).block();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("translatecontent", result);
        return resultMap;
    }
}