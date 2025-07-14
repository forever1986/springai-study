package com.demo.lesson28;

import com.alibaba.cloud.ai.reader.arxiv.ArxivDocumentReader;
import org.springframework.ai.document.Document;

import java.util.List;

public class ArxivReaderTest {
    public static void main(String[] args) {

        //两个参数，第一个是查询条件，第二个是返回文档数量
        ArxivDocumentReader arxivDocumentReader = new ArxivDocumentReader("Batch Normalization--1502.03167v3", 1);
        List<Document> documents = arxivDocumentReader.get();
        int i = 0;
        for (Document document : documents){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getMetadata().keySet());
            System.out.println(document.getText());
        }
        System.out.println("======== 总结 ============");
        System.out.println(arxivDocumentReader.getSummaries());
    }
}
