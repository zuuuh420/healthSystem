package com.health.vo;

import lombok.Data;
import java.util.List;

@Data
public class DashboardOverviewVO {
    private Integer healthScore;
    private String scoreLevel;
    private String summary;
    private Integer weeklyMinutes;
    private Double weeklyCalories;
    private Integer activeGoals;
    private Integer completedGoals;
    private Integer currentStreak;
    private List<SportStatsVO.DailyStat> trend;
    private List<String> suggestions;
}
