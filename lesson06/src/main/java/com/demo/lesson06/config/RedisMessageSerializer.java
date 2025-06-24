package com.demo.lesson06.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;

public class RedisMessageSerializer implements RedisSerializer<Message> {

    private final ObjectMapper objectMapper;
    private final JsonDeserializer<Message> messageDeserializer;

    public RedisMessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.messageDeserializer = new JsonDeserializer<>() {
            @Override
            public Message deserialize(JsonParser jp, DeserializationContext ctx)
                    throws IOException {
                ObjectNode root = jp.readValueAsTree();
                String type = root.get("messageType").asText();

                return switch (type) {
                    case "USER" -> new UserMessage(root.get("text").asText());
                    case "ASSISTANT" -> new AssistantMessage(root.get("text").asText());
                    case "SYSTEM" -> new SystemMessage(root.get("text").asText());
                    default -> throw new UnsupportedOperationException("消息类型错误");
                };
            }
        };
    }

    @Override
    public byte[] serialize(Message message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
    public Message deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return messageDeserializer.deserialize(objectMapper.getFactory().createParser(bytes), objectMapper.getDeserializationContext());
        } catch (Exception e) {
            throw new RuntimeException("反序列化识别", e);
        }
    }
}
