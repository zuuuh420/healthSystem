package com.health;

import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.controller.AdminController;
import com.health.controller.FoodController;
import com.health.dto.AdminStatsDTO;
import com.health.entity.User;
import com.health.service.AdminService;
import com.health.service.FoodService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminPermissionTest {

    @Autowired
    private AdminController adminController;

    @Autowired
    private FoodController foodController;

    @Autowired
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        // 默认不设置认证信息（模拟无认证状态）
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    // ==================== Admin端权限测试 ====================

    @Test
    void adminStats_未认证_应拒绝() {
        try {
            adminController.stats();
            fail("未认证用户访问管理端统计应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void adminStats_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            adminController.stats();
            fail("普通用户访问管理端统计应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void adminStats_管理员_应成功() {
        setUser(1L, "admin");
        Result<AdminStatsDTO> result = adminController.stats();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void listUsers_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            adminController.listUsers(null, 1, 10);
            fail("普通用户查看用户列表应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void listUsers_管理员_应成功() {
        setUser(1L, "admin");
        Result<?> result = adminController.listUsers(null, 1, 10);
        assertEquals(200, result.getCode());
    }

    @Test
    void updateRole_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            adminController.updateRole(3L, "admin");
            fail("普通用户修改角色应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void deleteUser_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            adminController.deleteUser(3L);
            fail("普通用户删除用户应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void resetPassword_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            adminController.resetPassword(3L);
            fail("普通用户重置密码应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    // ==================== 角色校验测试 ====================

    @Test
    void updateRole_无效角色值_应拒绝() {
        setUser(1L, "admin");
        try {
            adminService.updateUserRole(3L, "superadmin", 1L);
            fail("无效角色值应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("无效的角色值"));
        }
    }

    @Test
    void updateRole_修改自己角色_应拒绝() {
        setUser(1L, "admin");
        try {
            adminService.updateUserRole(1L, "user", 1L);
            fail("修改自己角色应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("不能修改自己的角色"));
        }
    }

    @Test
    void deleteUser_删除自己_应拒绝() {
        setUser(1L, "admin");
        try {
            adminService.deleteUser(1L, 1L);
            fail("删除自己应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("不能删除自己的账号"));
        }
    }

    // ==================== Food写操作权限测试 ====================

    @Test
    void foodCreate_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            foodController.create(new com.health.entity.Food());
            fail("普通用户创建食物应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void foodUpdate_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            foodController.update(1L, new com.health.entity.Food());
            fail("普通用户修改食物应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void foodDelete_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            foodController.delete(1L);
            fail("普通用户删除食物应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    @Test
    void foodBatchImport_普通用户_应拒绝() {
        setUser(2L, "user");
        try {
            foodController.batchImport(new ArrayList<>());
            fail("普通用户批量导入应该被拒绝");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("权限不足"));
        }
    }

    // ==================== 辅助方法 ====================

    private void setUser(Long userId, String role) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        authentication.setDetails("testuser:" + role);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
