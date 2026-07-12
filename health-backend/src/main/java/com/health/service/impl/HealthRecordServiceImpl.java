package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthRecord;
import com.health.mapper.HealthRecordMapper;
import com.health.service.HealthRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public boolean addHealthRecord(HealthRecord record) {
        return save(record);
    }

    @Override
    public boolean updateHealthRecord(HealthRecord record, Long userId) {
        HealthRecord existing = getById(record.getId());
        if (existing == null || !existing.getUserId().equals(userId)) return false;
        return updateById(record);
    }

    @Override
    public boolean deleteHealthRecord(Long id, Long userId) {
        HealthRecord existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) return false;
        return removeById(id);
    }

    @Override
    public List<HealthRecord> listByUser(Long userId) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId).orderByDesc(HealthRecord::getRecordDate);
        return list(wrapper);
    }

    @Override
    public List<HealthRecord> listByDateRange(Long userId, LocalDate start, LocalDate end) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId)
               .between(HealthRecord::getRecordDate, start, end)
               .orderByDesc(HealthRecord::getRecordDate);
        return list(wrapper);
    }
}
