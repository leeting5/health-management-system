package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.dto.UserProfileRequest;
import com.health.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author health-team
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @return 注册结果
     */
    Map<String, Object> register(String username, String password, String nickname);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（含token和用户信息）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User getByUsername(String username);

    /**
     * 更新用户信息
     *
     * @param request 用户资料
     * @return 是否成功
     */
    boolean updateUserInfo(UserProfileRequest request);

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(String oldPassword, String newPassword);
}
