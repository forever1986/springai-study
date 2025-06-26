package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class TextReaderTest {

    public static void main(String[] args) {
        String fileName = "data/textreaderdata.txt";
        Resource resource = new ClassPathResource(fileName);
        // 获取全部数据
        TextReader textReader = new TextReader(resource);
        // 设置Metadata数据，可用于检索后确认来自哪个文档
        textReader.getCustomMetadata().put("filename", fileName);
        List<Document> documents = textReader.get();
        for (Document document : documents){
            System.out.println(document.getMetadata().get("filename"));
            System.out.println(document.getText());
        }
    }
}
