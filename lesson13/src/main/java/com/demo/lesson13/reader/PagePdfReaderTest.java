package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class PagePdfReaderTest {

    public static void main(String[] args) {
        Resource resource = new ClassPathResource("data/pagepdfreaderdata.pdf");
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1) // 如果设置为0，则表示所有页都变成一个文档
                        .build());
        List<Document> documents = pdfReader.get();
        int i = 0;
        for (Document document : documents){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getMetadata().keySet());
            System.out.println(document.getText());
        }
    }

}
