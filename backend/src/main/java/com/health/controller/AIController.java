package com.health.controller;

import com.health.annotation.OperationLog;
import com.health.dto.AIChatRequest;
import com.health.dto.CreateConversationRequest;
import com.health.dto.HealthAdviceRequest;
import com.health.dto.RenameConversationRequest;
import com.health.entity.AiConversation;
import com.health.entity.AiMessage;
import com.health.entity.Result;
import com.health.service.AiConversationService;
import com.health.service.DeepSeekService;
import com.health.utils.UserHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * AI 健康助手控制器
 * 处理AI问诊、健康建议等接口
 *
 * @author health-team
 */
@RestController
@RequestMapping("/ai")
@Validated
public class AIController {

    @Resource
    private DeepSeekService deepSeekService;

    @Resource
    private AiConversationService conversationService;

    /**
     * 创建新会话
     */
    @PostMapping("/conversations")
    @OperationLog("创建AI会话")
    public Result<AiConversation> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        Long userId = UserHolder.getUserId();
        AiConversation conversation = conversationService.createConversation(userId, request.getTitle());
        return Result.success("会话创建成功", conversation);
    }

    /**
     * 获取当前用户的会话列表
     */
    @GetMapping("/conversations")
    public Result<List<AiConversation>> listConversations() {
        Long userId = UserHolder.getUserId();
        return Result.success(conversationService.listConversations(userId));
    }

    /**
     * 重命名会话
     */
    @PutMapping("/conversations/{id}")
    @OperationLog("重命名AI会话")
    public Result<String> renameConversation(@PathVariable Long id,
                                              @Valid @RequestBody RenameConversationRequest request) {
        Long userId = UserHolder.getUserId();
        conversationService.renameConversation(userId, id, request.getTitle());
        return Result.success("会话名称已修改");
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{id}")
    @OperationLog("删除AI会话")
    public Result<String> deleteConversation(@PathVariable Long id) {
        Long userId = UserHolder.getUserId();
        conversationService.deleteConversation(userId, id);
        return Result.success("会话已删除");
    }

    /**
     * 获取指定会话的消息列表
     */
    @GetMapping("/conversations/{id}/messages")
    public Result<List<AiMessage>> getMessages(@PathVariable Long id) {
        Long userId = UserHolder.getUserId();
        return Result.success(conversationService.getMessages(userId, id));
    }

    /**
     * 在指定会话中发送消息
     */
    @PostMapping("/conversations/{id}/chat")
    public Result<String> chat(@PathVariable Long id,
                               @Valid @RequestBody AIChatRequest request) {
        Long userId = UserHolder.getUserId();
        String reply = deepSeekService.chat(userId, id, request.getMessage().trim());
        return Result.success(reply);
    }

    /**
     * 健康分析接口
     * 传入健康数据，AI生成个性化分析建议
     *
     * @param request 健康建议请求
     * @return AI健康分析
     */
    @PostMapping("/health-advice")
    public Result<String> getHealthAdvice(@Valid @RequestBody HealthAdviceRequest request) {
        Long userId = UserHolder.getUserId();
        String advice = deepSeekService.generateHealthAdvice(userId, request.getHealthData());
        return Result.success(advice);
    }
}
