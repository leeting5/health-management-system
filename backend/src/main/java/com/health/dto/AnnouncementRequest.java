package com.health.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 公告创建/更新请求
 *
 * @author health-team
 */
@Data
public class AnnouncementRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题不能超过100字")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 2000, message = "公告内容不能超过2000字")
    private String content;

    private Integer status;
}
