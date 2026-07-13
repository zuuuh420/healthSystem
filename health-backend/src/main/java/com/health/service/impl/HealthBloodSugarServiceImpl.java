package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthBloodSugar;
import com.health.mapper.HealthBloodSugarMapper;
import com.health.service.HealthBloodSugarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HealthBloodSugarServiceImpl
        extends ServiceImpl<HealthBloodSugarMapper, HealthBloodSugar>
        implements HealthBloodSugarService {

    @Autowired
    private HealthBloodSugarMapper mapper;

    @Override
    public IPage<HealthBloodSugar> getList(Long userId, Integer pageNum, Integer pageSize,
                                           LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthBloodSugar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthBloodSugar::getUserId, userId);
        if (startDate != null) wrapper.ge(HealthBloodSugar::getRecordDate, startDate);
        if (endDate != null) wrapper.le(HealthBloodSugar::getRecordDate, endDate);
        wrapper.orderByDesc(HealthBloodSugar::getRecordDate);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<HealthBloodSugar> getTrend(Long userId, int limit) {
        return mapper.selectRecent(userId, limit);
    }

    @Override
    public HealthBloodSugar getById(Long id, Long userId) {
        HealthBloodSugar r = super.getById(id);
        return (r != null && r.getUserId().equals(userId)) ? r : null;
    }

    @Override public boolean add(HealthBloodSugar r) { return save(r); }

    @Override
    public boolean update(HealthBloodSugar r, Long userId) {
        HealthBloodSugar e = super.getById(r.getId());
        if (e == null || !e.getUserId().equals(userId)) return false;
        return updateById(r);
    }

    @Override
    public boolean delete(Long id, Long userId) {
        HealthBloodSugar e = super.getById(id);
        if (e == null || !e.getUserId().equals(userId)) return false;
        return removeById(id);
    }
}
