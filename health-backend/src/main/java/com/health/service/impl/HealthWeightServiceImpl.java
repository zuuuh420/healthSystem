package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthWeight;
import com.health.mapper.HealthWeightMapper;
import com.health.service.HealthWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthWeightServiceImpl extends ServiceImpl<HealthWeightMapper, HealthWeight>
        implements HealthWeightService {

    @Autowired
    private HealthWeightMapper healthWeightMapper;

    @Override
    public IPage<HealthWeight> getWeightList(Long userId, Integer pageNum, Integer pageSize,
                                             LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthWeight> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWeight::getUserId, userId);
        if (startDate != null) wrapper.ge(HealthWeight::getRecordDate, startDate);
        if (endDate != null) wrapper.le(HealthWeight::getRecordDate, endDate);
        wrapper.orderByDesc(HealthWeight::getRecordDate);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<HealthWeight> getWeightTrend(Long userId, int limit) {
        return healthWeightMapper.selectRecent(userId, limit);
    }

    @Override
    public HealthWeight getWeightById(Long id, Long userId) {
        HealthWeight record = getById(id);
        return (record != null && record.getUserId().equals(userId)) ? record : null;
    }

    @Override
    public boolean addWeight(HealthWeight record) {
        return save(record);
    }

    @Override
    public boolean updateWeight(HealthWeight record, Long userId) {
        HealthWeight existing = getById(record.getId());
        if (existing == null || !existing.getUserId().equals(userId)) return false;
        return updateById(record);
    }

    @Override
    public boolean deleteWeight(Long id, Long userId) {
        HealthWeight existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) return false;
        return removeById(id);
    }
}
