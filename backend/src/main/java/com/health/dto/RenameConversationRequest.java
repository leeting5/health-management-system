package com.health.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 重命名 AI 会话请求
 *
 * @author health-team
 */
@Data
public class RenameConversationRequest {

    @NotBlank(message = "会话名称不能为空")
    @Size(max = 100, message = "会话名称不能超过100字")
    private String title;
}
