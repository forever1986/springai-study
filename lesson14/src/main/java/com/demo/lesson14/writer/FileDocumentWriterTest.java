package com.demo.lesson14.writer;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.writer.FileDocumentWriter;

import java.io.IOException;
import java.util.List;

public class FileDocumentWriterTest {

    public static void main(String[] args) throws IOException {
        // 初始化文档
        Document doc = new Document("9月3日，这是一个值得永远纪念的日子。举行纪念活动，正是为了铭记历史、缅怀先烈，弘扬伟大爱国主义精神、伟大抗战精神。咱们共同关注、一起期待，以更好汲取前行的智慧和力量。");
        // 写入数据
        FileDocumentWriter writer = new FileDocumentWriter(
                "output.txt", // 文件名字
                true, // 把DocumentMarkers写入，会在文档头部写入Marker信息
                MetadataMode.ALL, //metadata数据
                true // 是否追加
        );
        writer.accept(List.of(doc));
    }

}
