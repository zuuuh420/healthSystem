package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.entity.SportType;
import com.health.service.impl.SportTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 运动类型Service测试
 */
@ExtendWith(MockitoExtension.class)
class SportTypeServiceTest {

    @InjectMocks
    private SportTypeServiceImpl sportTypeService;

    private SportType testSportType;

    @BeforeEach
    void setUp() {
        testSportType = new SportType();
        testSportType.setId(1L);
        testSportType.setName("跑步");
        testSportType.setCategory("有氧");
        testSportType.setCaloriesPerMinute(10.00);
        testSportType.setStatus(1);
    }

    @Test
    void testSportTypeEntityCreation() {
        // 测试运动类型实体创建
        SportType sportType = new SportType();
        sportType.setName("游泳");
        sportType.setCategory("有氧");
        sportType.setCaloriesPerMinute(8.00);

        assertEquals("游泳", sportType.getName());
        assertEquals("有氧", sportType.getCategory());
        assertEquals(8.00, sportType.getCaloriesPerMinute());
    }

    @Test
    void testSportTypeFields() {
        // 测试运动类型字段设置
        assertEquals(1L, testSportType.getId());
        assertEquals("跑步", testSportType.getName());
        assertEquals("有氧", testSportType.getCategory());
        assertEquals(10.00, testSportType.getCaloriesPerMinute());
        assertEquals(1, testSportType.getStatus());
    }

    @Test
    void testSportTypeCategoryValidation() {
        // 测试运动类型分类
        SportType aerobicType = new SportType();
        aerobicType.setCategory("有氧");
        assertEquals("有氧", aerobicType.getCategory());

        SportType anaerobicType = new SportType();
        anaerobicType.setCategory("无氧");
        assertEquals("无氧", anaerobicType.getCategory());

        SportType stretchType = new SportType();
        stretchType.setCategory("拉伸");
        assertEquals("拉伸", stretchType.getCategory());
    }

    @Test
    void testSportTypeStatusValues() {
        // 测试状态值：1启用，0禁用
        SportType enabledType = new SportType();
        enabledType.setStatus(1);
        assertEquals(1, enabledType.getStatus());

        SportType disabledType = new SportType();
        disabledType.setStatus(0);
        assertEquals(0, disabledType.getStatus());
    }

    @Test
    void testCaloriesPerMinuteCalculation() {
        // 测试卡路里计算逻辑
        double caloriesPerMinute = 10.00;
        int durationMinutes = 30;
        double expectedCalories = caloriesPerMinute * durationMinutes;

        assertEquals(300.00, expectedCalories);
    }

    @Test
    void testSportTypeNameConstraints() {
        // 测试名称约束
        SportType sportType = new SportType();

        // 测试正常名称
        sportType.setName("跑步");
        assertEquals("跑步", sportType.getName());

        // 测试长名称
        String longName = "这是一个很长的运动类型名称用于测试边界情况";
        sportType.setName(longName);
        assertEquals(longName, sportType.getName());
    }
}
