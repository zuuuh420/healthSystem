package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.SportPlan;

/**
 * 运动计划Service接口
 */
public interface SportPlanService extends IService<SportPlan> {

    /**
     * 分页查询运动计划
     */
    IPage<SportPlan> getSportPlans(Long userId, Integer pageNum, Integer pageSize, Integer status);

    /**
     * 创建运动计划
     */
    boolean addSportPlan(SportPlan sportPlan);

    /**
     * 修改运动计划
     */
    boolean updateSportPlan(SportPlan sportPlan, Long userId);

    /**
     * 删除运动计划
     */
    boolean deleteSportPlan(Long id, Long userId);
}
