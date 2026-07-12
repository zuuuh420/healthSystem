package com.health.dto;

import lombok.Data;

@Data
public class AdminStatsDTO {
    private long totalUsers;
    private long totalHealthRecords;
    private long totalDietRecords;
    private long totalSportRecords;
    private long totalFoods;
}
