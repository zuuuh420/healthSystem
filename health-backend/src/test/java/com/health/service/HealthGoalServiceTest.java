package com.health.service;

import com.health.entity.HealthGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 健康目标Service测试
 */
@ExtendWith(MockitoExtension.class)
class HealthGoalServiceTest {

    private HealthGoal testGoal;

    @BeforeEach
    void setUp() {
        testGoal = new HealthGoal();
        testGoal.setId(1L);
        testGoal.setUserId(100L);
        testGoal.setGoalType("weight");
        testGoal.setGoalName("减重目标");
        testGoal.setTargetValue(70.0);
        testGoal.setCurrentValue(75.0);
        testGoal.setUnit("kg");
        testGoal.setStartDate(LocalDate.now());
        testGoal.setEndDate(LocalDate.now().plusMonths(3));
        testGoal.setStatus(0);
    }

    @Test
    void testHealthGoalEntityCreation() {
        // 测试健康目标实体创建
        HealthGoal goal = new HealthGoal();
        goal.setUserId(100L);
        goal.setGoalType("exercise");
        goal.setGoalName("每周运动目标");
        goal.setTargetValue(150.0);
        goal.setUnit("分钟");

        assertEquals(100L, goal.getUserId());
        assertEquals("exercise", goal.getGoalType());
        assertEquals("每周运动目标", goal.getGoalName());
        assertEquals(150.0, goal.getTargetValue());
        assertEquals("分钟", goal.getUnit());
    }

    @Test
    void testHealthGoalFields() {
        // 测试健康目标字段设置
        assertEquals(1L, testGoal.getId());
        assertEquals(100L, testGoal.getUserId());
        assertEquals("weight", testGoal.getGoalType());
        assertEquals("减重目标", testGoal.getGoalName());
        assertEquals(70.0, testGoal.getTargetValue());
        assertEquals(75.0, testGoal.getCurrentValue());
        assertEquals("kg", testGoal.getUnit());
        assertEquals(0, testGoal.getStatus());
    }

    @Test
    void testGoalProgressCalculation() {
        // 测试目标进度计算
        double targetValue = 70.0;
        double currentValue = 75.0;
        double startValue = 80.0;

        // 计算进度百分比
        double progress = ((startValue - currentValue) / (startValue - targetValue)) * 100;
        assertEquals(50.0, progress);

        // 设置进度
        testGoal.setProgress(progress);
        assertEquals(50.0, testGoal.getProgress());
    }

    @Test
    void testGoalStatusValues() {
        // 测试目标状态值
        HealthGoal inProgressGoal = new HealthGoal();
        inProgressGoal.setStatus(0);
        assertEquals(0, inProgressGoal.getStatus());

        HealthGoal completedGoal = new HealthGoal();
        completedGoal.setStatus(1);
        assertEquals(1, completedGoal.getStatus());

        HealthGoal expiredGoal = new HealthGoal();
        expiredGoal.setStatus(2);
        assertEquals(2, expiredGoal.getStatus());
    }

    @Test
    void testGoalTypeOptions() {
        // 测试目标类型选项
        testGoal.setGoalType("weight");
        assertEquals("weight", testGoal.getGoalType());

        testGoal.setGoalType("exercise");
        assertEquals("exercise", testGoal.getGoalType());

        testGoal.setGoalType("steps");
        assertEquals("steps", testGoal.getGoalType());

        testGoal.setGoalType("sleep");
        assertEquals("sleep", testGoal.getGoalType());
    }

    @Test
    void testGoalDateRange() {
        // 测试目标日期范围
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        testGoal.setStartDate(startDate);
        testGoal.setEndDate(endDate);

        assertEquals(startDate, testGoal.getStartDate());
        assertEquals(endDate, testGoal.getEndDate());
        assertTrue(testGoal.getEndDate().isAfter(testGoal.getStartDate()));
    }
}
