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
        validateHealthRecord(record);
        calculateBmi(record);
        return save(record);
    }

    @Override
    public boolean updateHealthRecord(HealthRecord record, Long userId) {
        HealthRecord existing = getById(record.getId());
        if (existing == null || !existing.getUserId().equals(userId)) return false;
        validateHealthRecord(record);
        calculateBmi(record);
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

    private void calculateBmi(HealthRecord record) {
        if (record.getWeight() != null && record.getHeight() != null && record.getHeight() > 0) {
            double heightM = record.getHeight() / 100.0;
            double bmi = record.getWeight() / (heightM * heightM);
            record.setBmi(Math.round(bmi * 10.0) / 10.0);
        }
    }

    private void validateHealthRecord(HealthRecord record) {
        if (record.getWeight() != null && record.getWeight() <= 0) {
            throw new RuntimeException("体重必须大于0");
        }
        if (record.getHeight() != null && (record.getHeight() < 50 || record.getHeight() > 250)) {
            throw new RuntimeException("身高范围应在50-250cm之间");
        }
        if (record.getSystolicPressure() != null && record.getDiastolicPressure() != null) {
            if (record.getSystolicPressure() <= record.getDiastolicPressure()) {
                throw new RuntimeException("收缩压（高压）必须大于舒张压（低压）");
            }
            if (record.getSystolicPressure() < 60 || record.getSystolicPressure() > 260) {
                throw new RuntimeException("收缩压范围应在60-260mmHg之间");
            }
            if (record.getDiastolicPressure() < 30 || record.getDiastolicPressure() > 160) {
                throw new RuntimeException("舒张压范围应在30-160mmHg之间");
            }
        }
        if (record.getBloodSugar() != null && record.getBloodSugar() <= 0) {
            throw new RuntimeException("血糖值必须大于0");
        }
        if (record.getHeartRate() != null && (record.getHeartRate() < 30 || record.getHeartRate() > 220)) {
            throw new RuntimeException("心率范围应在30-220次/分之间");
        }
    }
}
