package com.demo.lesson27.controller;

import com.alibaba.cloud.ai.constant.Constant;
import com.alibaba.cloud.ai.dbconnector.DbConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.request.SchemaInitRequest;
import com.alibaba.cloud.ai.service.simple.SimpleVectorStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
public class SimpleChatController {


    @Autowired
    private StateGraph nl2sqlGraph;

    @Autowired
    private SimpleVectorStoreService simpleVectorStoreService;

    @Autowired
    private DbConfig dbConfig;

    @GetMapping("/simpleChat")
    public String simpleNl2Sql(@RequestParam(value = "message", defaultValue = "查询每个分类下已经成交且销量最高的商品及其销售总量，每个分类只返回销量最高的商品。", required = true) String message) throws Exception {
        SchemaInitRequest schemaInitRequest = new SchemaInitRequest();
        // 配置数据库
        schemaInitRequest.setDbConfig(dbConfig);
        // 将表名和字段等信息放入向量数据库
        schemaInitRequest.setTables(Arrays.asList("categories","order_items","orders","products","users","product_categories"));
        simpleVectorStoreService.schema(schemaInitRequest);
        // 执行图
        CompiledGraph compiledGraph = nl2sqlGraph.compile();
        Optional<OverAllState> invoke = compiledGraph.invoke(Map.of(Constant.INPUT_KEY, message));
        // 结果返回
        OverAllState overAllState = invoke.get();
        return overAllState.value(Constant.RESULT).get().toString();
    }
}
