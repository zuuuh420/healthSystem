package com.health.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CalorieStatsDTO {
    private Double todayCalories;
    private Double todayProtein;
    private Double todayFat;
    private Double todayCarbs;
    private Double weeklyAvgCalories;
    private Double monthlyAvgCalories;
    private List<Map<String, Object>> dailyCalories;
}
