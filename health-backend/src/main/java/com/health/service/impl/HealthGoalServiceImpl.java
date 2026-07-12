package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthGoal;
import com.health.mapper.HealthGoalMapper;
import com.health.service.HealthGoalService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 健康目标Service实现类
 */
@Service
public class HealthGoalServiceImpl extends ServiceImpl<HealthGoalMapper, HealthGoal> implements HealthGoalService {

    @Override
    public IPage<HealthGoal> getHealthGoals(Long userId, Integer pageNum, Integer pageSize, Integer status, String goalType) {
        LambdaQueryWrapper<HealthGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthGoal::getUserId, userId);

        if (status != null) {
            wrapper.eq(HealthGoal::getStatus, status);
        }

        if (StringUtils.hasText(goalType)) {
            wrapper.eq(HealthGoal::getGoalType, goalType);
        }

        wrapper.orderByDesc(HealthGoal::getCreateTime);

        // 分页查询
        Page<HealthGoal> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, wrapper);

        // 计算进度百分比
        page.getRecords().forEach(goal -> {
            if (goal.getTargetValue() != null && goal.getTargetValue() > 0) {
                double progress = (goal.getCurrentValue() / goal.getTargetValue()) * 100;
                goal.setProgress(Math.min(progress, 100));
            }
        });

        return page;
    }

    @Override
    public boolean addHealthGoal(HealthGoal healthGoal) {
        healthGoal.setCurrentValue(0.0);
        healthGoal.setStatus(0);
        return save(healthGoal);
    }

    @Override
    public boolean updateHealthGoal(HealthGoal healthGoal, Long userId) {
        // 验证目标属于当前用户
        HealthGoal existing = getById(healthGoal.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        return updateById(healthGoal);
    }

    @Override
    public boolean deleteHealthGoal(Long id, Long userId) {
        // 验证目标属于当前用户
        HealthGoal existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public boolean updateGoalProgress(Long id, Long userId, Double currentValue) {
        // 验证目标属于当前用户
        HealthGoal existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }

        existing.setCurrentValue(currentValue);

        // 判断是否完成
        if (currentValue >= existing.getTargetValue()) {
            existing.setStatus(1);
        }
        // 判断是否过期
        else if (existing.getEndDate() != null && existing.getEndDate().isBefore(LocalDate.now())) {
            existing.setStatus(2);
        }

        return updateById(existing);
    }
}
