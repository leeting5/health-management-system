package com.health.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI 健康建议请求
 *
 * @author health-team
 */
@Data
public class HealthAdviceRequest {

    @NotBlank(message = "健康数据不能为空")
    @Size(max = 5000, message = "健康数据不能超过5000字")
    private String healthData;
}
