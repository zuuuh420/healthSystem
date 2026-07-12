package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.SportPlanDTO;
import com.health.entity.SportPlan;
import com.health.service.SportPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 运动计划Controller
 */
@RestController
@RequestMapping("/sport-plans")
public class SportPlanController {

    @Autowired
    private SportPlanService sportPlanService;

    /**
     * 获取运动计划列表
     */
    @GetMapping
    public Result<IPage<SportPlan>> getSportPlans(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<SportPlan> page = sportPlanService.getSportPlans(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 创建运动计划
     */
    @PostMapping
    public Result<SportPlan> addSportPlan(@RequestBody SportPlanDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        SportPlan plan = new SportPlan();
        BeanUtils.copyProperties(dto, plan);
        plan.setUserId(userId);

        boolean success = sportPlanService.addSportPlan(plan);
        if (success) {
            return Result.success("创建成功", plan);
        }
        return Result.error("创建失败");
    }

    /**
     * 修改运动计划
     */
    @PutMapping("/{id}")
    public Result<Void> updateSportPlan(@PathVariable Long id, @RequestBody SportPlanDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        SportPlan plan = new SportPlan();
        BeanUtils.copyProperties(dto, plan);
        plan.setId(id);

        boolean success = sportPlanService.updateSportPlan(plan, userId);
        if (success) {
            return Result.success("修改成功", null);
        }
        return Result.error("修改失败");
    }

    /**
     * 删除运动计划
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSportPlan(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = sportPlanService.deleteSportPlan(id, userId);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }
}
