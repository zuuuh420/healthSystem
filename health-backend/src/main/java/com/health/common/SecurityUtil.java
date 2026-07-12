package com.health.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof String) {
            String details = (String) authentication.getDetails();
            int idx = details.indexOf(':');
            return idx > 0 ? details.substring(0, idx) : details;
        }
        return null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof String) {
            String details = (String) authentication.getDetails();
            int idx = details.indexOf(':');
            return idx > 0 ? details.substring(idx + 1) : "user";
        }
        return "user";
    }

    /**
     * 检查当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return "admin".equals(getCurrentUserRole());
    }

    /**
     * 检查当前用户是否为管理员，不是则抛出异常
     */
    public static void requireAdmin() {
        if (!isAdmin()) {
            throw new RuntimeException("权限不足，仅管理员可执行此操作");
        }
    }
}
