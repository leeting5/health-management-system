package com.health.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.health.entity.AiConversation;
import com.health.entity.AiMessage;

import javax.annotation.Resource;

/**
 * DeepSeek AI 对话服务
 * 提供AI健康问诊功能
 *
 * @author health-team
 */
@Slf4j
@Service
public class DeepSeekService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.max-tokens}")
    private Integer maxTokens;

    @Value("${deepseek.temperature}")
    private Double temperature;

    @Resource
    private AiConversationService conversationService;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    private static final int MAX_HISTORY_SIZE = 10; // 最多保留10轮对话

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 系统提示词：设定AI为健康助手
     */
    private static final String SYSTEM_PROMPT = "你是一位专业的健康助手，基于用户提供的健康数据和问题，提供科学、温和的健康建议。" +
            "请遵循以下原则：\n" +
            "1. 你的回答仅供健康参考，不能替代专业医疗诊断或治疗建议\n" +
            "2. 如果用户提到严重症状（如胸痛、呼吸困难、持续高烧等），请立即建议用户就医\n" +
            "3. 回答要简洁、易懂，避免使用过于专业的医学术语\n" +
            "4. 结合中国居民健康素养，给出实用的生活方式建议（饮食、运动、作息等）\n" +
            "5. 每次回答开头不需要重复免责声明，但结尾请简要提醒：以上建议仅供参考，如有不适请及时就医\n" +
            "6. 语气亲切温和，像一位关心用户的健康顾问";

    /**
     * 发送对话消息（非流式）
     *
     * @param userId 用户ID
     * @param message 用户消息
     * @return AI回复
     */
    public String chat(Long userId, Long conversationId, String message) {
        conversationService.getOwnedConversation(userId, conversationId);

        // 添加用户消息
        conversationService.appendMessage(conversationId, "user", message);
        List<AiMessage> messages = conversationService.getRecentMessages(conversationId, MAX_HISTORY_SIZE);

        // 构建请求消息列表（系统提示词 + 历史对话）
        List<Map<String, String>> requestMessages = new ArrayList<>();
        requestMessages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        for (AiMessage historyMessage : messages) {
            requestMessages.add(Map.of(
                    "role", historyMessage.getRole(),
                    "content", historyMessage.getContent()
            ));
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", requestMessages,
                    "max_tokens", maxTokens,
                    "temperature", temperature,
                    "stream", false
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("DeepSeek API error: status={}, body={}", response.statusCode(), response.body());
                // 如果 API key 未配置，返回配置提示
                if (!StringUtils.hasText(apiKey)) {
                    return "⚠️ AI 健康助手尚未配置 API Key。\n\n" +
                            "请通过环境变量 DEEPSEEK_API_KEY 配置你的 DeepSeek API Key：\n" +
                            "获取 API Key：https://platform.deepseek.com/";
                }
                return "抱歉，AI 服务暂时不可用，请稍后再试。（错误码：" + response.statusCode() + "）";
            }

            // 解析响应
            JsonNode root = objectMapper.readTree(response.body());
            String reply = root.path("choices").get(0).path("message").path("content").asText();

            // 添加AI回复到历史
            conversationService.appendMessage(conversationId, "assistant", reply);

            return reply;

        } catch (Exception e) {
            log.error("DeepSeek API request failed", e);
            return "抱歉，AI 服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 基于用户健康数据生成个性化健康建议
     *
     * @param userId 用户ID
     * @param healthData 健康数据摘要
     * @return 个性化建议
     */
    public String generateHealthAdvice(Long userId, String healthData) {
        String prompt = "根据我的以下健康数据，给我一份个性化的健康分析和建议：\n\n" + healthData;
        AiConversation conversation = conversationService.createConversation(userId, "健康分析");
        return chat(userId, conversation.getId(), prompt);
    }
}
