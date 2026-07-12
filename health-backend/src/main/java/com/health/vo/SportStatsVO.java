package com.health.vo;

import lombok.Data;
import java.util.List;

/**
 * 运动统计VO
 */
@Data
public class SportStatsVO {

    /**
     * 统计天数
     */
    private Integer days;

    /**
     * 总运动时长（分钟）
     */
    private Integer totalMinutes;

    /**
     * 总消耗卡路里
     */
    private Double totalCalories;

    /**
     * 运动记录数
     */
    private Integer recordCount;

    /**
     * 每日统计列表
     */
    private List<DailyStat> dailyStats;

    /**
     * 平均每日运动时长
     */
    private Double avgMinutesPerDay;

    /**
     * 运动分类统计
     */
    private List<CategoryStat> categoryStats;

    @Data
    public static class DailyStat {
        private String date;
        private Integer minutes;
        private Double calories;
    }

    @Data
    public static class CategoryStat {
        private String category;
        private Integer minutes;
        private Double calories;
        private Double percentage;
    }
}
