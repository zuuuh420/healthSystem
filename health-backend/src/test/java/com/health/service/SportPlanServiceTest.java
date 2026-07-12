package com.health.service;

import com.health.entity.SportPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 运动计划Service测试
 */
@ExtendWith(MockitoExtension.class)
class SportPlanServiceTest {

    private SportPlan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new SportPlan();
        testPlan.setId(1L);
        testPlan.setUserId(100L);
        testPlan.setSportTypeId(1L);
        testPlan.setPlanName("晨跑计划");
        testPlan.setFrequency("每天");
        testPlan.setTargetMinutes(30);
        testPlan.setStartDate(LocalDate.now());
        testPlan.setEndDate(LocalDate.now().plusWeeks(4));
        testPlan.setStatus(1);
    }

    @Test
    void testSportPlanEntityCreation() {
        // 测试运动计划实体创建
        SportPlan plan = new SportPlan();
        plan.setUserId(100L);
        plan.setPlanName("健身计划");
        plan.setTargetMinutes(60);

        assertEquals(100L, plan.getUserId());
        assertEquals("健身计划", plan.getPlanName());
        assertEquals(60, plan.getTargetMinutes());
    }

    @Test
    void testSportPlanFields() {
        // 测试运动计划字段设置
        assertEquals(1L, testPlan.getId());
        assertEquals(100L, testPlan.getUserId());
        assertEquals(1L, testPlan.getSportTypeId());
        assertEquals("晨跑计划", testPlan.getPlanName());
        assertEquals("每天", testPlan.getFrequency());
        assertEquals(30, testPlan.getTargetMinutes());
        assertEquals(1, testPlan.getStatus());
    }

    @Test
    void testPlanDateRange() {
        // 测试计划日期范围
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(4);

        testPlan.setStartDate(startDate);
        testPlan.setEndDate(endDate);

        assertEquals(startDate, testPlan.getStartDate());
        assertEquals(endDate, testPlan.getEndDate());
        assertTrue(testPlan.getEndDate().isAfter(testPlan.getStartDate()));
    }

    @Test
    void testPlanStatusValues() {
        // 测试计划状态值
        SportPlan activePlan = new SportPlan();
        activePlan.setStatus(1);
        assertEquals(1, activePlan.getStatus());

        SportPlan completedPlan = new SportPlan();
        completedPlan.setStatus(2);
        assertEquals(2, completedPlan.getStatus());

        SportPlan expiredPlan = new SportPlan();
        expiredPlan.setStatus(3);
        assertEquals(3, expiredPlan.getStatus());
    }

    @Test
    void testPlanFrequencyOptions() {
        // 测试频率选项
        testPlan.setFrequency("每天");
        assertEquals("每天", testPlan.getFrequency());

        testPlan.setFrequency("每周3次");
        assertEquals("每周3次", testPlan.getFrequency());

        testPlan.setFrequency("每周5次");
        assertEquals("每周5次", testPlan.getFrequency());
    }

    @Test
    void testTransientFieldSportTypeName() {
        // 测试非数据库字段
        testPlan.setSportTypeName("跑步");
        assertEquals("跑步", testPlan.getSportTypeName());
    }
}
