package com.health.service;

import java.util.List;
import java.util.Map;

/**
 * AI 会话历史存储接口
 *
 * @author health-team
 */
public interface AiConversationStore {

    /**
     * 获取指定用户的会话历史
     */
    List<Map<String, String>> getHistory(Long userId);

    /**
     * 追加一条会话消息
     */
    void appendMessage(Long userId, Map<String, String> message);

    /**
     * 清空指定用户的会话历史
     */
    void clearHistory(Long userId);
}
