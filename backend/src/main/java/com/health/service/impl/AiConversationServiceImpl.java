package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.common.BusinessException;
import com.health.entity.AiConversation;
import com.health.entity.AiMessage;
import com.health.mapper.AiConversationMapper;
import com.health.mapper.AiMessageMapper;
import com.health.service.AiConversationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI 多会话服务实现类
 *
 * @author health-team
 */
@Service
public class AiConversationServiceImpl implements AiConversationService {

    @Resource
    private AiConversationMapper conversationMapper;

    @Resource
    private AiMessageMapper messageMapper;

    @Override
    public List<AiConversation> listConversations(Long userId) {
        LambdaQueryWrapper<AiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiConversation::getUserId, userId)
                .orderByDesc(AiConversation::getUpdateTime);
        return conversationMapper.selectList(wrapper);
    }

    @Override
    public AiConversation createConversation(Long userId, String title) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setTitle(StringUtils.hasText(title) ? title.trim() : "新对话");
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public void renameConversation(Long userId, Long conversationId, String title) {
        AiConversation conversation = getOwnedConversation(userId, conversationId);
        conversation.setTitle(title.trim());
        conversationMapper.updateById(conversation);
    }

    @Override
    public void deleteConversation(Long userId, Long conversationId) {
        getOwnedConversation(userId, conversationId);
        conversationMapper.deleteById(conversationId);

        LambdaQueryWrapper<AiMessage> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(AiMessage::getConversationId, conversationId);
        messageMapper.delete(messageWrapper);
    }

    @Override
    public List<AiMessage> getMessages(Long userId, Long conversationId) {
        getOwnedConversation(userId, conversationId);
        LambdaQueryWrapper<AiMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiMessage::getConversationId, conversationId)
                .orderByAsc(AiMessage::getCreateTime)
                .orderByAsc(AiMessage::getId);
        return messageMapper.selectList(wrapper);
    }

    @Override
    public List<AiMessage> getRecentMessages(Long conversationId, int limit) {
        LambdaQueryWrapper<AiMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiMessage::getConversationId, conversationId)
                .orderByDesc(AiMessage::getCreateTime)
                .orderByDesc(AiMessage::getId)
                .last("LIMIT " + Math.max(limit, 1));
        List<AiMessage> messages = messageMapper.selectList(wrapper);
        java.util.Collections.reverse(messages);
        return messages;
    }

    @Override
    public AiMessage appendMessage(Long conversationId, String role, String content) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);
        return message;
    }

    @Override
    public AiConversation getOwnedConversation(Long userId, Long conversationId) {
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw BusinessException.notFound("会话不存在");
        }
        if (!conversation.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权限访问该会话");
        }
        return conversation;
    }
}
