package com.health.service.impl;

import com.health.service.AiConversationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的 AI 会话历史存储，适合本地开发和没有 Redis 的环境。
 *
 * @author health-team
 */
@Component
@ConditionalOnProperty(name = "app.ai.redis-enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryAiConversationStore implements AiConversationStore {

    private final Map<Long, List<Map<String, String>>> cache = new ConcurrentHashMap<>();

    @Override
    public List<Map<String, String>> getHistory(Long userId) {
        List<Map<String, String>> messages = cache.get(userId);
        if (messages == null) {
            return new ArrayList<>();
        }
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }

    @Override
    public void appendMessage(Long userId, Map<String, String> message) {
        List<Map<String, String>> messages = cache.computeIfAbsent(
                userId,
                key -> Collections.synchronizedList(new ArrayList<>())
        );
        synchronized (messages) {
            messages.add(message);
        }
    }

    @Override
    public void clearHistory(Long userId) {
        cache.remove(userId);
    }
}
