package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.common.BusinessException;
import com.health.common.ErrorCode;
import com.health.dto.UserProfileRequest;
import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.service.UserService;
import com.health.utils.JwtUtils;
import com.health.utils.PasswordUtils;
import com.health.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 *
 * @author health-team
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> register(String username, String password, String nickname) {
        Map<String, Object> result = new HashMap<>();

        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw BusinessException.badRequest("用户名和密码不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw BusinessException.badRequest("用户名长度需在3-20位之间");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw BusinessException.badRequest("密码长度需在6-20位之间");
        }

        // 检查用户名是否已存在
        User existUser = getByUsername(username);
        if (existUser != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在，请更换用户名");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encode(password));
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setGender(0);
        user.setRole("USER");
        save(user);

        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw BusinessException.badRequest("用户名和密码不能为空");
        }

        // 查询用户
        User user = getByUsername(username);
        if (user == null) {
            throw BusinessException.badRequest("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtils.matches(password, user.getPassword())) {
            throw BusinessException.badRequest("用户名或密码错误");
        }

        // 历史 MD5 密码登录成功后自动升级为 PBKDF2
        if (PasswordUtils.isLegacyHash(user.getPassword())) {
            user.setPassword(PasswordUtils.encode(password));
            updateById(user);
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回用户信息（不返回密码）
        user.setPassword(null);
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public boolean updateUserInfo(UserProfileRequest request) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        User user = new User();
        user.setId(userId);
        user.setNickname(request.getNickname());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        return updateById(user);
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        User user = getById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw BusinessException.badRequest("旧密码错误");
        }

        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw BusinessException.badRequest("新密码长度需在6-20位之间");
        }

        // 更新密码
        user.setPassword(PasswordUtils.encode(newPassword));
        return updateById(user);
    }
}
