package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.AdminStatsDTO;
import com.health.entity.User;
import com.health.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public Result<AdminStatsDTO> stats() {
        SecurityUtil.requireAdmin();
        return Result.success(adminService.getStats());
    }

    @GetMapping("/users")
    public Result<IPage<User>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        SecurityUtil.requireAdmin();
        IPage<User> result = adminService.listUsers(keyword, page, size);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestParam String role) {
        SecurityUtil.requireAdmin();
        adminService.updateUserRole(id, role, SecurityUtil.getCurrentUserId());
        return Result.success("角色修改成功", null);
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        SecurityUtil.requireAdmin();
        adminService.deleteUser(id, SecurityUtil.getCurrentUserId());
        return Result.success("删除成功", null);
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id) {
        SecurityUtil.requireAdmin();
        String newPassword = adminService.resetPassword(id);
        return Result.success("密码已重置", newPassword);
    }
}
