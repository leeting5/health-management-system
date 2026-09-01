package com.health.service.impl;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 内存 AI 会话历史存储单元测试
 */
class InMemoryAiConversationStoreTest {

    @Test
    void appendAndGetHistoryShouldReturnMessagesInOrder() {
        InMemoryAiConversationStore store = new InMemoryAiConversationStore();

        store.appendMessage(1L, Map.of("role", "user", "content", "你好"));
        store.appendMessage(1L, Map.of("role", "assistant", "content", "你好，有什么可以帮你？"));

        List<Map<String, String>> history = store.getHistory(1L);
        assertEquals(2, history.size());
        assertEquals("user", history.get(0).get("role"));
        assertEquals("assistant", history.get(1).get("role"));
    }

    @Test
    void clearHistoryShouldRemoveUserMessages() {
        InMemoryAiConversationStore store = new InMemoryAiConversationStore();
        store.appendMessage(1L, Map.of("role", "user", "content", "test"));
        store.clearHistory(1L);

        assertTrue(store.getHistory(1L).isEmpty());
    }
}
