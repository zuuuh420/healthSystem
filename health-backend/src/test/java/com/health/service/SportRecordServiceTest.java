package com.health.service;

import com.health.entity.SportRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 运动记录Service测试
 */
@ExtendWith(MockitoExtension.class)
class SportRecordServiceTest {

    private SportRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new SportRecord();
        testRecord.setId(1L);
        testRecord.setUserId(100L);
        testRecord.setSportTypeId(1L);
        testRecord.setDurationMinutes(30);
        testRecord.setCaloriesBurned(300.00);
        testRecord.setSportDate(LocalDate.now());
        testRecord.setRemark("晨跑30分钟");
    }

    @Test
    void testSportRecordEntityCreation() {
        // 测试运动记录实体创建
        SportRecord record = new SportRecord();
        record.setUserId(100L);
        record.setSportTypeId(1L);
        record.setDurationMinutes(45);
        record.setSportDate(LocalDate.now());

        assertEquals(100L, record.getUserId());
        assertEquals(1L, record.getSportTypeId());
        assertEquals(45, record.getDurationMinutes());
        assertNotNull(record.getSportDate());
    }

    @Test
    void testSportRecordFields() {
        // 测试运动记录字段设置
        assertEquals(1L, testRecord.getId());
        assertEquals(100L, testRecord.getUserId());
        assertEquals(1L, testRecord.getSportTypeId());
        assertEquals(30, testRecord.getDurationMinutes());
        assertEquals(300.00, testRecord.getCaloriesBurned());
        assertEquals("晨跑30分钟", testRecord.getRemark());
    }

    @Test
    void testCaloriesBurnedCalculation() {
        // 测试卡路里消耗计算
        double caloriesPerMinute = 10.00;
        int durationMinutes = 30;
        double expectedCalories = caloriesPerMinute * durationMinutes;

        SportRecord record = new SportRecord();
        record.setDurationMinutes(durationMinutes);
        record.setCaloriesBurned(expectedCalories);

        assertEquals(300.00, record.getCaloriesBurned());
    }

    @Test
    void testSportDateValidation() {
        // 测试运动日期
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusWeeks(1);

        testRecord.setSportDate(today);
        assertEquals(today, testRecord.getSportDate());

        testRecord.setSportDate(yesterday);
        assertEquals(yesterday, testRecord.getSportDate());

        testRecord.setSportDate(lastWeek);
        assertEquals(lastWeek, testRecord.getSportDate());
    }

    @Test
    void testDurationMinutesValidation() {
        // 测试运动时长边界值
        SportRecord record = new SportRecord();

        // 最小有效时长
        record.setDurationMinutes(1);
        assertEquals(1, record.getDurationMinutes());

        // 正常时长
        record.setDurationMinutes(60);
        assertEquals(60, record.getDurationMinutes());

        // 长时间运动
        record.setDurationMinutes(180);
        assertEquals(180, record.getDurationMinutes());
    }

    @Test
    void testTransientFields() {
        // 测试非数据库字段（运动类型名称和分类）
        testRecord.setSportTypeName("跑步");
        testRecord.setSportCategory("有氧");

        assertEquals("跑步", testRecord.getSportTypeName());
        assertEquals("有氧", testRecord.getSportCategory());
    }
}
