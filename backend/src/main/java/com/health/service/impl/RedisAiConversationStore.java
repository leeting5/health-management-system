package com.health.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.service.AiConversationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于 Redis 的 AI 会话历史存储，支持跨重启持久化和过期清理。
 *
 * @author health-team
 */
@Component
@ConditionalOnProperty(name = "app.ai.redis-enabled", havingValue = "true")
public class RedisAiConversationStore implements AiConversationStore {

    private static final String KEY_PREFIX = "health:ai:conversation:";
    private static final Duration TTL = Duration.ofHours(24);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<Map<String, String>> getHistory(Long userId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key(userId));
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void appendMessage(Long userId, Map<String, String> message) {
        List<Map<String, String>> history = getHistory(userId);
        history.add(message);
        try {
            stringRedisTemplate.opsForValue().set(
                    key(userId),
                    objectMapper.writeValueAsString(history),
                    TTL
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save AI conversation history", e);
        }
    }

    @Override
    public void clearHistory(Long userId) {
        stringRedisTemplate.delete(key(userId));
    }

    private String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
