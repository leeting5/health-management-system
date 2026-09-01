package com.health.controller;

import com.health.annotation.OperationLog;
import com.health.dto.ChangePasswordRequest;
import com.health.dto.LoginRequest;
import com.health.dto.RegisterRequest;
import com.health.dto.UserProfileRequest;
import com.health.entity.Result;
import com.health.entity.User;
import com.health.service.UserService;
import com.health.utils.UserHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户注册、登录、信息管理等接口
 *
 * @author health-team
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> result = userService.register(
                request.getUsername(), request.getPassword(), request.getNickname());
        return Result.success("注册成功", result);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录结果（含token）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", result);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     *
     * @param request 用户资料
     * @return 更新结果
     */
    @PutMapping("/info")
    @OperationLog("更新个人信息")
    public Result<String> updateUserInfo(@Valid @RequestBody UserProfileRequest request) {
        boolean success = userService.updateUserInfo(request);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    /**
     * 修改密码
     *
     * @param request 修改密码请求
     * @return 修改结果
     */
    @PutMapping("/password")
    @OperationLog("修改密码")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        boolean success = userService.changePassword(
                request.getOldPassword(), request.getNewPassword());
        return success ? Result.success("密码修改成功") : Result.fail("密码修改失败");
    }
}
