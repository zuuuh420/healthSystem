package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.dto.CalorieStatsDTO;
import com.health.dto.DietRecordDTO;
import com.health.entity.DietRecord;
import com.health.entity.Food;
import com.health.mapper.DietRecordMapper;
import com.health.mapper.FoodMapper;
import com.health.service.DietRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public DietRecord create(Long userId, DietRecordDTO dto) {
        Food food = foodMapper.selectById(dto.getFoodId());
        if (food == null) return null;

        DietRecord record = buildRecord(userId, dto, food);
        save(record);
        return record;
    }

    @Override
    public DietRecord update(Long id, Long userId, DietRecordDTO dto) {
        DietRecord record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) return null;

        Food food = foodMapper.selectById(dto.getFoodId());
        if (food == null) return null;

        DietRecord updated = buildRecord(userId, dto, food);
        updated.setId(id);
        updateById(updated);
        return updated;
    }

    @Override
    public void delete(Long id, Long userId) {
        DietRecord record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) return;
        removeById(id);
    }

    @Override
    public IPage<DietRecord> page(Long userId, LocalDate date, int p, int size) {
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DietRecord::getUserId, userId);
        if (date != null) {
            wrapper.eq(DietRecord::getRecordDate, date);
        }
        wrapper.orderByDesc(DietRecord::getRecordDate, DietRecord::getCreateTime);
        return baseMapper.selectPage(new Page<>(p, size), wrapper);
    }

    @Override
    public List<DietRecord> listByRange(Long userId, LocalDate start, LocalDate end) {
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DietRecord::getUserId, userId)
               .between(DietRecord::getRecordDate, start, end)
               .orderByDesc(DietRecord::getRecordDate, DietRecord::getCreateTime);
        return list(wrapper);
    }

    @Override
    public CalorieStatsDTO getCalorieStats(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(29);

        CalorieStatsDTO stats = new CalorieStatsDTO();
        stats.setTodayCalories(dietRecordMapper.sumCaloriesByDate(userId, today));

        Map<String, Object> nutrients = dietRecordMapper.sumNutrientsByDate(userId, today);
        if (nutrients != null) {
            stats.setTodayProtein(toDouble(nutrients.get("protein")));
            stats.setTodayFat(toDouble(nutrients.get("fat")));
            stats.setTodayCarbs(toDouble(nutrients.get("carbs")));
        }

        Double weekTotal = dietRecordMapper.sumCaloriesBetween(userId, weekStart, today);
        stats.setWeeklyAvgCalories(round(weekTotal / 7.0));

        Double monthTotal = dietRecordMapper.sumCaloriesBetween(userId, monthStart, today);
        stats.setMonthlyAvgCalories(round(monthTotal / 30.0));

        stats.setDailyCalories(dietRecordMapper.dailyCaloriesBetween(userId, weekStart, today));
        return stats;
    }

    private DietRecord buildRecord(Long userId, DietRecordDTO dto, Food food) {
        double ratio = dto.getQuantityG() / 100.0;
        DietRecord record = new DietRecord();
        record.setUserId(userId);
        record.setRecordDate(dto.getRecordDate());
        record.setMealType(dto.getMealType());
        record.setFoodId(food.getId());
        record.setFoodName(food.getName());
        record.setQuantityG(dto.getQuantityG());
        record.setCalories(round(food.getCaloriesPer100g() * ratio));
        record.setProtein(round(food.getProteinPer100g() * ratio));
        record.setFat(round(food.getFatPer100g() * ratio));
        record.setCarbs(round(food.getCarbsPer100g() * ratio));
        record.setNote(dto.getNote());
        return record;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;
        return ((Number) obj).doubleValue();
    }
}
