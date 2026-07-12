package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.HealthGoalDTO;
import com.health.entity.HealthGoal;
import com.health.service.HealthGoalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 健康目标Controller
 */
@RestController
@RequestMapping("/health-goals")
public class HealthGoalController {

    @Autowired
    private HealthGoalService healthGoalService;

    /**
     * 获取健康目标列表
     */
    @GetMapping
    public Result<IPage<HealthGoal>> getHealthGoals(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String goalType) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<HealthGoal> page = healthGoalService.getHealthGoals(userId, pageNum, pageSize, status, goalType);
        return Result.success(page);
    }

    /**
     * 创建健康目标
     */
    @PostMapping
    public Result<HealthGoal> addHealthGoal(@RequestBody HealthGoalDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        HealthGoal goal = new HealthGoal();
        BeanUtils.copyProperties(dto, goal);
        goal.setUserId(userId);

        boolean success = healthGoalService.addHealthGoal(goal);
        if (success) {
            return Result.success("创建成功", goal);
        }
        return Result.error("创建失败");
    }

    /**
     * 修改健康目标
     */
    @PutMapping("/{id}")
    public Result<Void> updateHealthGoal(@PathVariable Long id, @RequestBody HealthGoalDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        HealthGoal goal = new HealthGoal();
        BeanUtils.copyProperties(dto, goal);
        goal.setId(id);

        boolean success = healthGoalService.updateHealthGoal(goal, userId);
        if (success) {
            return Result.success("修改成功", null);
        }
        return Result.error("修改失败");
    }

    /**
     * 删除健康目标
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHealthGoal(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = healthGoalService.deleteHealthGoal(id, userId);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    /**
     * 更新目标进度
     */
    @PutMapping("/{id}/progress")
    public Result<Void> updateGoalProgress(@PathVariable Long id, @RequestParam Double currentValue) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = healthGoalService.updateGoalProgress(id, userId, currentValue);
        if (success) {
            return Result.success("进度更新成功", null);
        }
        return Result.error("进度更新失败");
    }
}
