package com.demo.lesson06.repository;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private static final String REDIS_KEY_PREFIX = "chatmemory:";

    private final RedisTemplate<String, Message> redisTemplate;

    public RedisChatMemoryRepository(RedisTemplate<String, Message> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<String> findConversationIds() {
        Set<String> keys = this.redisTemplate.keys(REDIS_KEY_PREFIX+"*");
        return keys.stream().toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return this.redisTemplate.opsForList().range(REDIS_KEY_PREFIX+conversationId, 0, -1);
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        this.redisTemplate.delete(REDIS_KEY_PREFIX+conversationId);// 由于每次的messages都会获取到之前的数据，因此要先删除，在插入
        this.redisTemplate.opsForList().rightPushAll(REDIS_KEY_PREFIX+conversationId, messages);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(REDIS_KEY_PREFIX + conversationId);
    }
}
