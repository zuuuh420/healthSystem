package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.common.JwtUtil;
import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existing = findByUsername(user.getUsername());
        if (existing != null) {
            return false;
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setRole("user");

        return save(user);
    }

    @Override
    public String login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            return null;
        }

        // 生成Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
}
