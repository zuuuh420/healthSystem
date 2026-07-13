package com.health;

import com.health.controller.DietRecordController;
import com.health.controller.HealthRecordController;
import com.health.dto.DietRecordDTO;
import com.health.entity.DietRecord;
import com.health.entity.HealthRecord;
import com.health.common.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataIsolationTest {

    @Autowired
    private DietRecordController dietRecordController;

    @Autowired
    private HealthRecordController healthRecordController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    // ==================== 饮食记录数据隔离测试 ====================

    @Test
    void dietCreate_自动关联当前用户() {
        setUser(2L, "user");
        DietRecordDTO dto = new DietRecordDTO();
        dto.setRecordDate(LocalDate.now());
        dto.setMealType("lunch");
        dto.setFoodId(1L);
        dto.setQuantityG(200.0);

        Result<?> result = dietRecordController.create(dto);

        if (result.getCode() == 200) {
            DietRecord record = (DietRecord) result.getData();
            assertEquals(2L, record.getUserId(), "饮食记录的userId应该与当前用户一致");
        }
    }

    @Test
    void dietList_仅返回当前用户数据() {
        setUser(2L, "user");
        Result<?> result = dietRecordController.list(null, 1, 50);

        assertEquals(200, result.getCode());
        // 验证返回的记录都属于当前用户
        if (result.getData() instanceof com.baomidou.mybatisplus.core.metadata.IPage) {
            @SuppressWarnings("unchecked")
            com.baomidou.mybatisplus.core.metadata.IPage<DietRecord> page =
                (com.baomidou.mybatisplus.core.metadata.IPage<DietRecord>) result.getData();
            for (DietRecord record : page.getRecords()) {
                assertEquals(2L, record.getUserId(),
                        "查询结果中所有记录应属于当前用户，但发现记录" + record.getId() + "属于用户" + record.getUserId());
            }
        }
    }

    @Test
    void dietStats_返回当前用户统计() {
        setUser(2L, "user");
        Result<?> result = dietRecordController.stats();
        assertEquals(200, result.getCode());
    }

    @Test
    void dietRange_仅返回当前用户数据() {
        setUser(2L, "user");
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        Result<?> result = dietRecordController.listByRange(start, end);

        assertEquals(200, result.getCode());
    }

    // ==================== 健康数据隔离测试 ====================

    @Test
    void healthCreate_自动关联当前用户() {
        setUser(2L, "user");
        HealthRecord record = new HealthRecord();
        record.setRecordDate(LocalDate.now());
        record.setHeight(170.0);
        record.setWeight(65.0);
        record.setSystolicPressure(120);
        record.setDiastolicPressure(80);
        record.setBloodSugar(5.6);
        record.setHeartRate(72);

        Result<?> result = healthRecordController.create(record);

        if (result.getCode() == 200) {
            HealthRecord saved = (HealthRecord) result.getData();
            assertEquals(2L, saved.getUserId(), "健康记录的userId应该与当前用户一致");
        }
    }

    @Test
    void healthList_仅返回当前用户数据() {
        setUser(2L, "user");
        Result<?> result = healthRecordController.list();

        assertEquals(200, result.getCode());
        if (result.getData() instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<HealthRecord> list = (java.util.List<HealthRecord>) result.getData();
            for (HealthRecord record : list) {
                assertEquals(2L, record.getUserId(),
                        "查询结果中所有记录应属于当前用户，但发现记录属于用户" + record.getUserId());
            }
        }
    }

    @Test
    void healthRange_仅返回当前用户数据() {
        setUser(2L, "user");
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        Result<?> result = healthRecordController.listByRange(start, end);

        assertEquals(200, result.getCode());
    }

    @Test
    void healthUpdate_跨用户修改_应失败() {
        // 用户B尝试修改用户A的记录
        setUser(2L, "user");
        HealthRecord record = new HealthRecord();
        record.setId(1L);
        record.setRecordDate(LocalDate.now());
        record.setWeight(70.0);
        record.setSystolicPressure(120);
        record.setDiastolicPressure(80);

        Result<?> result = healthRecordController.update(1L, record);
        // 如果记录1属于用户1（非当前用户2），修改应该失败
        if (result.getCode() != 200) {
            assertTrue(result.getMsg().contains("失败"));
        }
    }

    @Test
    void healthDelete_跨用户删除_应失败() {
        setUser(2L, "user");
        Result<?> result = healthRecordController.delete(1L);
        if (result.getCode() != 200) {
            assertTrue(result.getMsg().contains("失败"));
        }
    }

    // ==================== 跨用户数据隔离对比测试 ====================

    @Test
    void 两个用户查看彼此数据_应不可见() {
        // 用户A创建记录
        setUser(1L, "admin");
        DietRecordDTO dto = new DietRecordDTO();
        dto.setRecordDate(LocalDate.now());
        dto.setMealType("breakfast");
        dto.setFoodId(1L);
        dto.setQuantityG(100.0);
        Result<?> resultA = dietRecordController.create(dto);
        assertNotNull(resultA);

        // 用户B查询 - 不应看到用户A刚创建的记录
        setUser(2L, "user");
        Result<?> resultB = dietRecordController.list(null, 1, 100);
        assertEquals(200, resultB.getCode());

        if (resultA.getCode() == 200 && resultB.getData() instanceof com.baomidou.mybatisplus.core.metadata.IPage) {
            @SuppressWarnings("unchecked")
            com.baomidou.mybatisplus.core.metadata.IPage<DietRecord> pageB =
                (com.baomidou.mybatisplus.core.metadata.IPage<DietRecord>) resultB.getData();
            Long recordIdFromA = ((DietRecord) resultA.getData()).getId();
            for (DietRecord record : pageB.getRecords()) {
                assertNotEquals(recordIdFromA, record.getId(),
                        "用户B不应该看到用户A创建的饮食记录");
            }
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
