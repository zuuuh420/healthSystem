package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.SportPlan;
import com.health.entity.SportType;
import com.health.mapper.SportPlanMapper;
import com.health.mapper.SportTypeMapper;
import com.health.service.SportPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 运动计划Service实现类
 */
@Service
public class SportPlanServiceImpl extends ServiceImpl<SportPlanMapper, SportPlan> implements SportPlanService {

    @Autowired
    private SportPlanMapper sportPlanMapper;

    @Autowired
    private SportTypeMapper sportTypeMapper;

    @Override
    public IPage<SportPlan> getSportPlans(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<SportPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SportPlan::getUserId, userId);

        if (status != null) {
            wrapper.eq(SportPlan::getStatus, status);
        }

        wrapper.orderByDesc(SportPlan::getCreateTime);

        // 分页查询
        Page<SportPlan> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, wrapper);

        // 填充运动类型名称
        page.getRecords().forEach(plan -> {
            SportType sportType = sportTypeMapper.selectById(plan.getSportTypeId());
            if (sportType != null) {
                plan.setSportTypeName(sportType.getName());
            }
        });

        return page;
    }

    @Override
    public boolean addSportPlan(SportPlan sportPlan) {
        sportPlan.setStatus(1);
        return save(sportPlan);
    }

    @Override
    public boolean updateSportPlan(SportPlan sportPlan, Long userId) {
        // 验证计划属于当前用户
        SportPlan existing = getById(sportPlan.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        return updateById(sportPlan);
    }

    @Override
    public boolean deleteSportPlan(Long id, Long userId) {
        // 验证计划属于当前用户
        SportPlan existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }
}
