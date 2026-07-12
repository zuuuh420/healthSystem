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

@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements AdminService {

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
    public User updateUserRole(Long userId, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) return null;
        user.setRole(role);
        userMapper.updateById(user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "admin".equals(user.getRole())) return;
        userMapper.deleteById(userId);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return;
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        userMapper.updateById(user);
    }
}
