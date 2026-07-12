package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.User;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
