package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class TikaReaderTest {

    public static void main(String[] args) {
        Resource resource = new ClassPathResource("data/tikareaderdata.docx");
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.get();
        int i = 0;
        for (Document document : documents){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getMetadata().keySet());
            System.out.println(document.getText());
        }
    }

}
