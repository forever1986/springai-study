package com.demo.lesson11.advanced.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于智谱的rerank模型进行排序，官方文档：https://www.bigmodel.cn/dev/api/knowlage-manage/rerank
 */
public class RerankDocumentPostProcessor implements DocumentPostProcessor {

    private final String API_URL = "https://open.bigmodel.cn/api/paas/v4/rerank";
    private final String API_KEY = "720a5f832f4b4754ba4003cde0f92598.4BJ5wgObIG0CFdB5"; // 替换为实际API密钥

    private final RestTemplate restTemplate;

    public RerankDocumentPostProcessor(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public List<Document> process(Query query, List<Document> documents) {

        // 1. 将Document转为String
        List<String> strDocuments = new ArrayList<>();
        for(Document document:documents){
            strDocuments.add(document.getText());
        }

        // 2. 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", API_KEY); // 认证头

        // 3. 构建请求体
        RerankRequest request = new RerankRequest();
        request.setQuery(query.text());
        request.setTop_n(2);
        request.setDocuments(strDocuments);
        request.setReturn_documents(true); // 返回原文
        request.setReturn_raw_scores(false); // 返回原始分数

        // 4. 发送POST请求
        HttpEntity<RerankRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<RerankResponse> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                RerankResponse.class
        );

        // 5. 组装Document
        List<Document> list = new ArrayList<>();
        if(response.getBody().getResults()!=null){
            for(RerankResponse.RerankResult rerankResult : response.getBody().getResults()){
                Document document = new Document(rerankResult.getDocument());
                document.getMetadata().put("score", rerankResult.getRelevance_score());
                list.add(document);
            }
        }

        // 6. 返回解析后的响应
        return list;
    }


    // 1. 请求参数封装类
    static class RerankRequest {
        private String model = "rerank";  // 默认模型
        private String query;             // 用户查询文本
        private Integer top_n;            // 返回结果数量
        private List<String> documents;   // 候选文本列表
        private Boolean return_documents; // 是否返回原文
        private Boolean return_raw_scores;// 是否返回原始分数
        private String request_id;        // 请求ID
        private String user_id;           // 用户ID

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Integer getTop_n() {
            return top_n;
        }

        public void setTop_n(Integer top_n) {
            this.top_n = top_n;
        }

        public List<String> getDocuments() {
            return documents;
        }

        public void setDocuments(List<String> documents) {
            this.documents = documents;
        }

        public Boolean getReturn_documents() {
            return return_documents;
        }

        public void setReturn_documents(Boolean return_documents) {
            this.return_documents = return_documents;
        }

        public Boolean getReturn_raw_scores() {
            return return_raw_scores;
        }

        public void setReturn_raw_scores(Boolean return_raw_scores) {
            this.return_raw_scores = return_raw_scores;
        }

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

    // 2. 响应结果封装类
    public static class RerankResponse {
        private String request_id;
        private String id;
        private List<RerankResult> results;
        private TokenUsage usage;

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<RerankResult> getResults() {
            return results;
        }

        public void setResults(List<RerankResult> results) {
            this.results = results;
        }

        public TokenUsage getUsage() {
            return usage;
        }

        public void setUsage(TokenUsage usage) {
            this.usage = usage;
        }

        // 内部类：排序结果
        static class RerankResult {
            private Integer index;
            private Float relevance_score;
            private String document;
            public Integer getIndex() {
                return index;
            }

            public void setIndex(Integer index) {
                this.index = index;
            }

            public Float getRelevance_score() {
                return relevance_score;
            }

            public void setRelevance_score(Float relevance_score) {
                this.relevance_score = relevance_score;
            }

            public String getDocument() {
                return document;
            }

            public void setDocument(String document) {
                this.document = document;
            }
        }

        // 内部类：Token统计
        static class TokenUsage {
            private Integer prompt_tokens;
            private Integer total_tokens;
            public Integer getPrompt_tokens() {
                return prompt_tokens;
            }

            public void setPrompt_tokens(Integer prompt_tokens) {
                this.prompt_tokens = prompt_tokens;
            }

            public Integer getTotal_tokens() {
                return total_tokens;
            }

            public void setTotal_tokens(Integer total_tokens) {
                this.total_tokens = total_tokens;
            }
        }
    }
}
