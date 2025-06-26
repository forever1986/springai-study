package com.demo.lesson13.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

public class JsonReaderTest {

    public static void main(String[] args){
        Resource resource = new ClassPathResource("data/jsonreaderdata.json");
        // 1.获取全部数据
        JsonReader jsonReader1 = new JsonReader(resource);
        List<Document> documents1 = jsonReader1.get();
        int i =0;
        for (Document document : documents1){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getText());
        }
        System.out.println("=======================");
        // 2.获取其中某些字段
        JsonReader jsonReader2 = new JsonReader(resource,"id","name");
        List<Document> documents2 = jsonReader2.get();
        i =0;
        for (Document document : documents2){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getText());
        }
        System.out.println("=======================");
        // 3.路径+值获取某一条数据
        List<Document> documents3 = jsonReader2.get("/0"); //获取第一条
        i =0;
        for (Document document : documents3){
            System.out.println("--------"+(++i)+"--------");
            System.out.println(document.getText());
        }
    }
}
