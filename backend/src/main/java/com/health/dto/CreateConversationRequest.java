package com.health.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 创建 AI 会话请求
 *
 * @author health-team
 */
@Data
public class CreateConversationRequest {

    @Size(max = 100, message = "会话名称不能超过100字")
    private String title;
}
