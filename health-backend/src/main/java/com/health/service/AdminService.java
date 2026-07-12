package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.dto.AdminStatsDTO;
import com.health.entity.User;

public interface AdminService {
    AdminStatsDTO getStats();
    IPage<User> listUsers(String keyword, int page, int size);
    User updateUserRole(Long userId, String role);
    void deleteUser(Long userId);
    void resetPassword(Long userId);
}
