package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.entity.SportRecord;
import com.health.entity.SportType;
import com.health.mapper.SportRecordMapper;
import com.health.mapper.SportTypeMapper;
import com.health.service.SportStatsService;
import com.health.vo.SportStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 运动统计Service实现类
 */
@Service
public class SportStatsServiceImpl implements SportStatsService {

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private SportTypeMapper sportTypeMapper;

    @Override
    public SportStatsVO getWeeklyStats(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today;

        return getStatsByDateRange(userId, weekStart, weekEnd);
    }

    @Override
    public SportStatsVO getMonthlyStats(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today;

        SportStatsVO stats = getStatsByDateRange(userId, monthStart, monthEnd);

        // 计算平均每日运动时长
        if (stats.getTotalMinutes() != null && stats.getTotalMinutes() > 0) {
            long days = monthStart.until(monthEnd).getDays() + 1;
            stats.setAvgMinutesPerDay(Math.round(stats.getTotalMinutes() * 100.0 / days) / 100.0);
        }

        // 计算分类统计
        calculateCategoryStats(userId, monthStart, monthEnd, stats);

        return stats;
    }

    @Override
    public SportStatsVO getSportTrend(Long userId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查询日期范围内的运动记录
        LambdaQueryWrapper<SportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SportRecord::getUserId, userId)
               .ge(SportRecord::getSportDate, startDate)
               .le(SportRecord::getSportDate, endDate);

        List<SportRecord> records = sportRecordMapper.selectList(wrapper);

        // 按日期分组统计
        Map<LocalDate, List<SportRecord>> dateGroupMap = records.stream()
                .collect(Collectors.groupingBy(SportRecord::getSportDate));

        // 生成趋势数据
        List<SportStatsVO.DailyStat> trend = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            SportStatsVO.DailyStat dailyStat = new SportStatsVO.DailyStat();
            dailyStat.setDate(currentDate.toString());

            List<SportRecord> dayRecords = dateGroupMap.get(currentDate);
            if (dayRecords != null) {
                int totalMinutes = dayRecords.stream().mapToInt(SportRecord::getDurationMinutes).sum();
                double totalCalories = dayRecords.stream().mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned() : 0).sum();
                dailyStat.setMinutes(totalMinutes);
                dailyStat.setCalories(Math.round(totalCalories * 100.0) / 100.0);
            } else {
                dailyStat.setMinutes(0);
                dailyStat.setCalories(0.0);
            }

            trend.add(dailyStat);
            currentDate = currentDate.plusDays(1);
        }

        SportStatsVO stats = new SportStatsVO();
        stats.setDays(days);
        stats.setDailyStats(trend);

        return stats;
    }

    /**
     * 根据日期范围获取统计
     */
    private SportStatsVO getStatsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SportRecord::getUserId, userId)
               .ge(SportRecord::getSportDate, startDate)
               .le(SportRecord::getSportDate, endDate);

        List<SportRecord> records = sportRecordMapper.selectList(wrapper);

        int totalMinutes = records.stream().mapToInt(SportRecord::getDurationMinutes).sum();
        double totalCalories = records.stream().mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned() : 0).sum();

        // 按日期分组统计
        Map<LocalDate, List<SportRecord>> dateGroupMap = records.stream()
                .collect(Collectors.groupingBy(SportRecord::getSportDate));

        List<SportStatsVO.DailyStat> dailyStats = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            SportStatsVO.DailyStat dailyStat = new SportStatsVO.DailyStat();
            dailyStat.setDate(currentDate.toString());

            List<SportRecord> dayRecords = dateGroupMap.get(currentDate);
            if (dayRecords != null) {
                int dayMinutes = dayRecords.stream().mapToInt(SportRecord::getDurationMinutes).sum();
                double dayCalories = dayRecords.stream().mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned() : 0).sum();
                dailyStat.setMinutes(dayMinutes);
                dailyStat.setCalories(Math.round(dayCalories * 100.0) / 100.0);
            } else {
                dailyStat.setMinutes(0);
                dailyStat.setCalories(0.0);
            }

            dailyStats.add(dailyStat);
            currentDate = currentDate.plusDays(1);
        }

        SportStatsVO stats = new SportStatsVO();
        stats.setTotalMinutes(totalMinutes);
        stats.setTotalCalories(Math.round(totalCalories * 100.0) / 100.0);
        stats.setRecordCount(records.size());
        stats.setDailyStats(dailyStats);

        return stats;
    }

    /**
     * 计算运动分类统计
     */
    private void calculateCategoryStats(Long userId, LocalDate startDate, LocalDate endDate, SportStatsVO stats) {
        LambdaQueryWrapper<SportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SportRecord::getUserId, userId)
               .ge(SportRecord::getSportDate, startDate)
               .le(SportRecord::getSportDate, endDate);

        List<SportRecord> records = sportRecordMapper.selectList(wrapper);

        // 获取所有运动类型
        Map<Long, SportType> sportTypeMap = new HashMap<>();
        List<SportType> sportTypes = sportTypeMapper.selectList(null);
        for (SportType type : sportTypes) {
            sportTypeMap.put(type.getId(), type);
        }

        // 按分类分组
        Map<String, List<SportRecord>> categoryGroupMap = records.stream()
                .collect(Collectors.groupingBy(r -> {
                    SportType type = sportTypeMap.get(r.getSportTypeId());
                    return type != null ? type.getCategory() : "其他";
                }));

        int totalMinutes = stats.getTotalMinutes() != null ? stats.getTotalMinutes() : 0;
        List<SportStatsVO.CategoryStat> categoryStats = new ArrayList<>();

        for (Map.Entry<String, List<SportRecord>> entry : categoryGroupMap.entrySet()) {
            SportStatsVO.CategoryStat categoryStat = new SportStatsVO.CategoryStat();
            categoryStat.setCategory(entry.getKey());

            int categoryMinutes = entry.getValue().stream().mapToInt(SportRecord::getDurationMinutes).sum();
            double categoryCalories = entry.getValue().stream()
                    .mapToDouble(r -> r.getCaloriesBurned() != null ? r.getCaloriesBurned() : 0).sum();

            categoryStat.setMinutes(categoryMinutes);
            categoryStat.setCalories(Math.round(categoryCalories * 100.0) / 100.0);

            if (totalMinutes > 0) {
                categoryStat.setPercentage(Math.round(categoryMinutes * 10000.0 / totalMinutes) / 100.0);
            }

            categoryStats.add(categoryStat);
        }

        stats.setCategoryStats(categoryStats);
    }
}
