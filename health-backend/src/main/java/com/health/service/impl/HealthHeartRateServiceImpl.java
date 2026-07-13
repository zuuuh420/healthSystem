package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthHeartRate;
import com.health.mapper.HealthHeartRateMapper;
import com.health.service.HealthHeartRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthHeartRateServiceImpl
        extends ServiceImpl<HealthHeartRateMapper, HealthHeartRate>
        implements HealthHeartRateService {

    @Autowired
    private HealthHeartRateMapper mapper;

    @Override
    public IPage<HealthHeartRate> getList(Long userId, Integer pageNum, Integer pageSize,
                                          LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthHeartRate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthHeartRate::getUserId, userId);
        if (startDate != null) wrapper.ge(HealthHeartRate::getRecordDate, startDate);
        if (endDate != null) wrapper.le(HealthHeartRate::getRecordDate, endDate);
        wrapper.orderByDesc(HealthHeartRate::getRecordDate);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<HealthHeartRate> getTrend(Long userId, int limit) {
        return mapper.selectRecent(userId, limit);
    }

    @Override
    public HealthHeartRate getById(Long id, Long userId) {
        HealthHeartRate r = super.getById(id);
        return (r != null && r.getUserId().equals(userId)) ? r : null;
    }

    @Override public boolean add(HealthHeartRate r) { return save(r); }

    @Override
    public boolean update(HealthHeartRate r, Long userId) {
        HealthHeartRate e = super.getById(r.getId());
        if (e == null || !e.getUserId().equals(userId)) return false;
        return updateById(r);
    }

    @Override
    public boolean delete(Long id, Long userId) {
        HealthHeartRate e = super.getById(id);
        if (e == null || !e.getUserId().equals(userId)) return false;
        return removeById(id);
    }
}
