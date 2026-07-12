package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.dto.AdminStatsDTO;
import com.health.entity.User;

public interface AdminService {
    AdminStatsDTO getStats();
    IPage<User> listUsers(String keyword, int page, int size);
    void updateUserRole(Long userId, String role, Long operatorId);
    void deleteUser(Long userId, Long operatorId);
    String resetPassword(Long userId);
}
