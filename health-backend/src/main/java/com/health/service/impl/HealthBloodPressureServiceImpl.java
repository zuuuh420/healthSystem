package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthBloodPressure;
import com.health.mapper.HealthBloodPressureMapper;
import com.health.service.HealthBloodPressureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthBloodPressureServiceImpl
        extends ServiceImpl<HealthBloodPressureMapper, HealthBloodPressure>
        implements HealthBloodPressureService {

    @Autowired
    private HealthBloodPressureMapper mapper;

    @Override
    public IPage<HealthBloodPressure> getList(Long userId, Integer pageNum, Integer pageSize,
                                              LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthBloodPressure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthBloodPressure::getUserId, userId);
        if (startDate != null) wrapper.ge(HealthBloodPressure::getRecordDate, startDate);
        if (endDate != null) wrapper.le(HealthBloodPressure::getRecordDate, endDate);
        wrapper.orderByDesc(HealthBloodPressure::getRecordDate);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<HealthBloodPressure> getTrend(Long userId, int limit) {
        return mapper.selectRecent(userId, limit);
    }

    @Override
    public HealthBloodPressure getById(Long id, Long userId) {
        HealthBloodPressure r = super.getById(id);
        return (r != null && r.getUserId().equals(userId)) ? r : null;
    }

    @Override
    public boolean add(HealthBloodPressure record) { return save(record); }

    @Override
    public boolean update(HealthBloodPressure record, Long userId) {
        HealthBloodPressure e = super.getById(record.getId());
        if (e == null || !e.getUserId().equals(userId)) return false;
        return updateById(record);
    }

    @Override
    public boolean delete(Long id, Long userId) {
        HealthBloodPressure e = super.getById(id);
        if (e == null || !e.getUserId().equals(userId)) return false;
        return removeById(id);
    }
}
