package com.health.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新用户资料请求
 *
 * @author health-team
 */
@Data
public class UserProfileRequest {

    @Size(max = 50, message = "昵称长度不能超过50位")
    private String nickname;

    @Min(value = 0, message = "性别参数不合法")
    @Max(value = 2, message = "性别参数不合法")
    private Integer gender;

    @Min(value = 0, message = "年龄不能小于0")
    @Max(value = 150, message = "年龄不能超过150")
    private Integer age;

    @Pattern(regexp = "^$|^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 255, message = "头像地址不能超过255位")
    private String avatar;
}
