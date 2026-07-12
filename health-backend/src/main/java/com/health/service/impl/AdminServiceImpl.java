package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.dto.AdminStatsDTO;
import com.health.entity.User;
import com.health.mapper.*;
import com.health.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements AdminService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public AdminStatsDTO getStats() {
        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setTotalUsers(userMapper.selectCount(null));
        stats.setTotalHealthRecords(healthRecordMapper.selectCount(null));
        stats.setTotalDietRecords(dietRecordMapper.selectCount(null));
        stats.setTotalSportRecords(sportRecordMapper.selectCount(null));
        stats.setTotalFoods(foodMapper.selectCount(null));
        return stats;
    }

    @Override
    public IPage<User> listUsers(String keyword, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void updateUserRole(Long userId, String role, Long operatorId) {
        if (!"admin".equals(role) && !"user".equals(role)) {
            throw new RuntimeException("无效的角色值，仅允许 admin 或 user");
        }
        if (userId.equals(operatorId)) {
            throw new RuntimeException("不能修改自己的角色");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long userId, Long operatorId) {
        if (userId.equals(operatorId)) {
            throw new RuntimeException("不能删除自己的账号");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            long adminCount = countAdmins();
            if (adminCount <= 1) {
                throw new RuntimeException("不能删除最后一个管理员");
            }
        }
        userMapper.deleteById(userId);
    }

    @Override
    public String resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String newPassword = generateRandomPassword(8);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userMapper.updateById(user);
        return newPassword;
    }

    private long countAdmins() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "admin");
        return userMapper.selectCount(wrapper);
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
