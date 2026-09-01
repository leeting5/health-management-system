package com.health.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 从当前请求中获取登录用户信息
 *
 * @author health-team
 */
public class UserHolder {

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userId = request.getAttribute("userId");
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object username = request.getAttribute("username");
            return username != null ? username.toString() : null;
        }
        return null;
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getRole() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object role = request.getAttribute("role");
            return role != null ? role.toString() : null;
        }
        return null;
    }
}
