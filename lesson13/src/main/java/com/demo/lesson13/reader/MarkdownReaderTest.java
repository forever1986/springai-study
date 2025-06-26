package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class MarkdownReaderTest {

    public static void main(String[] args) {
        Resource resource = new ClassPathResource("data/markdownreaderdata.md");

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true) //当设置为 true 时，Markdown中的水平分隔线"---"，将创建新的 Document 对象。
                .withIncludeCodeBlock(false) //当设置为 true 时，代码块将包含在与周围文本相同的文档中。当设置为 false 时，代码块会创建单独的文档对象。
                .withIncludeBlockquote(false) //当设置为 true 时，引用块将与周围的文本包含在同一个文档中。当设置为 false 时，引用块将创建单独的文档对象。
                .withAdditionalMetadata("filename", "data/markdownreaderdata.md")//允许您向所有创建的“文档”对象添加自定义元数据。
                .build();

        MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
        List<Document> documents = markdownDocumentReader.get();
        int i = 0;
        for (Document document : documents){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getMetadata().keySet());
            System.out.println(document.getText());
        }
    }
}
