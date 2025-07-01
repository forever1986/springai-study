package com.demo.lesson11.advanced.preretrieval;

import com.demo.lesson11.advanced.utils.BuildModelUtils;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;

public class TranslationQueryTransformerTest {

    public static void main(String[] args) {
        Query query = new Query("Which city is the capital of the United States?");
        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder())
                .targetLanguage("Chinese")
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery.text());
    }
}
