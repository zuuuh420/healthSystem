package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.SportRecord;
import com.health.entity.SportType;
import com.health.mapper.SportRecordMapper;
import com.health.mapper.SportTypeMapper;
import com.health.service.SportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录Service实现类
 */
@Service
public class SportRecordServiceImpl extends ServiceImpl<SportRecordMapper, SportRecord> implements SportRecordService {

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private SportTypeMapper sportTypeMapper;

    @Override
    public IPage<SportRecord> getSportRecords(Long userId, Integer pageNum, Integer pageSize,
                                               LocalDate startDate, LocalDate endDate, Long sportTypeId) {
        LambdaQueryWrapper<SportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SportRecord::getUserId, userId);

        // 日期范围筛选
        if (startDate != null) {
            wrapper.ge(SportRecord::getSportDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(SportRecord::getSportDate, endDate);
        }

        // 运动类型筛选
        if (sportTypeId != null) {
            wrapper.eq(SportRecord::getSportTypeId, sportTypeId);
        }

        wrapper.orderByDesc(SportRecord::getSportDate).orderByDesc(SportRecord::getCreateTime);

        // 分页查询
        Page<SportRecord> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, wrapper);

        // 填充运动类型名称
        page.getRecords().forEach(record -> {
            SportType sportType = sportTypeMapper.selectById(record.getSportTypeId());
            if (sportType != null) {
                record.setSportTypeName(sportType.getName());
                record.setSportCategory(sportType.getCategory());
            }
        });

        return page;
    }

    @Override
    public SportRecord getSportRecordById(Long id, Long userId) {
        SportRecord record = getById(id);
        if (record != null && record.getUserId().equals(userId)) {
            SportType sportType = sportTypeMapper.selectById(record.getSportTypeId());
            if (sportType != null) {
                record.setSportTypeName(sportType.getName());
                record.setSportCategory(sportType.getCategory());
            }
            return record;
        }
        return null;
    }

    @Override
    public boolean addSportRecord(SportRecord sportRecord) {
        // 计算消耗卡路里
        SportType sportType = sportTypeMapper.selectById(sportRecord.getSportTypeId());
        if (sportType != null && sportType.getCaloriesPerMinute() != null) {
            double calories = sportRecord.getDurationMinutes() * sportType.getCaloriesPerMinute();
            sportRecord.setCaloriesBurned(calories);
        }

        return save(sportRecord);
    }

    @Override
    public boolean updateSportRecord(SportRecord sportRecord, Long userId) {
        // 验证记录属于当前用户
        SportRecord existing = getById(sportRecord.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }

        // 重新计算卡路里
        SportType sportType = sportTypeMapper.selectById(sportRecord.getSportTypeId());
        if (sportType != null && sportType.getCaloriesPerMinute() != null) {
            double calories = sportRecord.getDurationMinutes() * sportType.getCaloriesPerMinute();
            sportRecord.setCaloriesBurned(calories);
        }

        return updateById(sportRecord);
    }

    @Override
    public boolean deleteSportRecord(Long id, Long userId) {
        // 验证记录属于当前用户
        SportRecord existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public List<SportRecord> getTodayRecords(Long userId) {
        return sportRecordMapper.selectTodayRecords(userId, LocalDate.now());
    }
}
