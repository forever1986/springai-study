package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class JsoupReaderTest {

    public static void main(String[] args) {
        Resource resource = new ClassPathResource("data/jsoupreaderdata.html");
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p") // 通过selector，标记只获取什么标签内容，这里是提取<article>标签的p（p在html代表段落）
                .charset("ISO-8859-1")  // 使用 ISO-8859-1 编码
                .includeLinkUrls(true) // 在metadata（元数据）中包括LinkURL
                .metadataTags(List.of("author", "date")) // 提取author和date到metadata（元数据）
                .additionalMetadata("source", "data/jsoupreaderdata.html") // 添加一个用户自定义的metadata（元数据），这把文件名放入
                .build();

        JsoupDocumentReader jsoupDocumentReader = new JsoupDocumentReader(resource, config);
        List<Document> documents = jsoupDocumentReader.get();
        int i =0;
        for (Document document : documents){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getMetadata().keySet());
            System.out.println(document.getText());
        }
    }
}
