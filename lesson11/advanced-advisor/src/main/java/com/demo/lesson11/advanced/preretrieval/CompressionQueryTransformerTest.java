package com.demo.lesson11.advanced.preretrieval;

import com.demo.lesson11.advanced.utils.BuildModelUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;

public class CompressionQueryTransformerTest {

    public static void main(String[] args) {

        // 这是原始查询
        Query query = Query.builder()
                .text("第二大城市是哪个？")
                .history(new UserMessage("中国的首都是哪个城市？"),
                        new AssistantMessage("北京是中国的首都"))
                .build();
        // 提示模版
        PromptTemplate promptTemplate = new PromptTemplate("""
                根据以下对话历史和后续的查询，您的任务是综合提炼出一个简洁、独立的查询语句，该语句应包含历史对话中的相关信息。
                确保独立的查询语句清晰、具体，并能准确反映用户意图。
                对话历史：
                {history}
                后续询问：{query}
                
                独立查询：
                """);
        // 使用CompressionQueryTransformer转换
        QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder()) // 需要引入大模型
                .promptTemplate(promptTemplate) // 自定义模版
                .build();
        System.out.println(queryTransformer.apply(query).text());
    }

}
