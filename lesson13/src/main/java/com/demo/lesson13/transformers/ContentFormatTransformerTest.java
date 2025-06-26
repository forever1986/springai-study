package com.demo.lesson13.transformers;

import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.ContentFormatTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class ContentFormatTransformerTest {

    public static void main(String[] args) {

        // 加载文档
        List<Document> documents = loadDocument();

        // 文档分块
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(50) // 每个文本分块所包含的token数量的目标值（默认值：800）
                .withMinChunkSizeChars(10) //每个文本块的最小字符数（默认值：350）
                .withMinChunkLengthToEmbed(1) //要包含的分块的最小长度（默认值：5）
                .withMaxNumChunks(3) //从文本中生成的分块的最大数量（默认值：10000）；实际数量比设置多1个
                .withKeepSeparator(true) //保留分隔符：是否在分块中保留分隔符（如换行符）（默认值：true）
                .build();
        List<Document> splitDocuments = splitter.apply(documents);


        // 增加ContentFormatTransformer，过来掉元数据的filename标签
        DefaultContentFormatter formatter = DefaultContentFormatter.builder()
                .withExcludedInferenceMetadataKeys("filename")
                .build();
        ContentFormatTransformer contentFormatTransformer = new ContentFormatTransformer(formatter);
        List<Document> formatDocuments = contentFormatTransformer.apply(splitDocuments);


        // 打印
        int i = 0;
        for (Document doc : formatDocuments) {
            System.out.println("--------"+(++i)+"--------");
            // 获取过滤filename标签的数据
            String format = doc.getFormattedContent(MetadataMode.INFERENCE);
            System.out.println(format);
        }
    }

    // 加载文本
    private static List<Document> loadDocument(){
        String fileName = "data/tokentextsplitterdata.txt";
        Resource resource = new ClassPathResource(fileName);
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", fileName);
        return textReader.get();
    }
}
