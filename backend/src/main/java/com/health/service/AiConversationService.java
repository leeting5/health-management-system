package com.health.service;

import com.health.entity.AiConversation;
import com.health.entity.AiMessage;

import java.util.List;

/**
 * AI 多会话服务接口
 *
 * @author health-team
 */
public interface AiConversationService {

    List<AiConversation> listConversations(Long userId);

    AiConversation createConversation(Long userId, String title);

    void renameConversation(Long userId, Long conversationId, String title);

    void deleteConversation(Long userId, Long conversationId);

    List<AiMessage> getMessages(Long userId, Long conversationId);

    List<AiMessage> getRecentMessages(Long conversationId, int limit);

    AiMessage appendMessage(Long conversationId, String role, String content);

    AiConversation getOwnedConversation(Long userId, Long conversationId);
}
