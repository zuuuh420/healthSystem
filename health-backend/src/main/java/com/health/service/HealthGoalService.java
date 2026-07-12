package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthGoal;

/**
 * 健康目标Service接口
 */
public interface HealthGoalService extends IService<HealthGoal> {

    /**
     * 分页查询健康目标
     */
    IPage<HealthGoal> getHealthGoals(Long userId, Integer pageNum, Integer pageSize, Integer status, String goalType);

    /**
     * 创建健康目标
     */
    boolean addHealthGoal(HealthGoal healthGoal);

    /**
     * 修改健康目标
     */
    boolean updateHealthGoal(HealthGoal healthGoal, Long userId);

    /**
     * 删除健康目标
     */
    boolean deleteHealthGoal(Long id, Long userId);

    /**
     * 更新目标进度
     */
    boolean updateGoalProgress(Long id, Long userId, Double currentValue);
}
