package com.demo.lesson11.advanced.controller;

import com.demo.lesson11.advanced.config.RRFFusionProcessor;
import com.demo.lesson11.advanced.config.RerankDocumentPostProcessor;
import com.demo.lesson11.advanced.utils.BuildModelUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class RelevancyEvaluatorController {

    private ChatClient chatClient;

    public RelevancyEvaluatorController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, RestTemplateBuilder builder) {

        // 1. Pre-Retrieval模块
        // 1.1 QueryTransformer：将问题转换为中文
        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder())
                .targetLanguage("Chinese")
                .build();
        // 1.2 MultiQueryExpander：将问题变成多个角度的问题
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(BuildModelUtils.getBuilder()) // 需要引入大模型
                .numberOfQueries(3) // 生成新的问题条数
                .includeOriginal(true) // 是否包括原先问题，默认是true
                .build();

        // 2. Retrieval模块
        // 2.1 VectorStoreDocumentRetriever：查询向量数据库，相似度大于等于0.3的前2条记录，并且来自官方网站的文档
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.3)
                .topK(2)
                .filterExpression(new FilterExpressionBuilder().eq("source", "官方网站").build())
                .build();
        // 2.2 自定义DocumentJoiner：将多个查询结果的文档采用RRF算法进行去重和排序，
        DocumentJoiner documentJoiner = new DocumentJoiner(){
            private final RRFFusionProcessor fusionProcessor = new RRFFusionProcessor(60);
            @Override
            public List<Document> join(Map<Query, List<List<Document>>> documentsForQuery) {
                if(documentsForQuery!=null){
                    List<List<Document>> documents = new ArrayList<>();
                    Iterator<Query> iterator = documentsForQuery.keySet().iterator();
                    while (iterator.hasNext()){
                        List<List<Document>> lists = documentsForQuery.get(iterator.next());
                        if(lists!=null){
                            documents.add(lists.get(0));
                        }
                    }
                    return fusionProcessor.fuseRankings(documents);
                }else{
                    return List.of();
                }
            }
        };

        // 3. Post-Retrieval模块
        // 3.1 自定义DocumentPostProcessor：基于智谱的rerank模型进行排序
        DocumentPostProcessor RRFPostProcessor = new RerankDocumentPostProcessor(builder);

        // 4. Generation模块
        // 4.1 QueryAugmenter：组装最后的问题
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


        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(queryTransformer) // 注册queryTransformer
                .queryExpander(queryExpander) // 注册queryExpander
                .documentRetriever(documentRetriever) // 注册documentRetriever
                .documentJoiner(documentJoiner) // 注册documentJoiner
                .documentPostProcessors(RRFPostProcessor) // 注册documentPostProcessor
                .queryAugmenter(queryAugmenter) // 注册queryAugmenter
                .build();

        this.chatClient = chatClientBuilder
                // 通过Advisors方式，对向量数据库进行封装
                .defaultAdvisors(ragAdvisor)
                .build();
    }

    /**
     * @param message 问题
     */
    @GetMapping("/ai/rag")
    public String rag(@RequestParam(value = "message", required = true, defaultValue = "What country's large model is ChatGLM3?") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
