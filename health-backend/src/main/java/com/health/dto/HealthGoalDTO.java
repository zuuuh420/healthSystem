package com.health.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 健康目标DTO
 */
@Data
public class HealthGoalDTO {

    private Long id;

    private String goalType;

    private String goalName;

    private Double targetValue;

    private String unit;

    private LocalDate startDate;

    private LocalDate endDate;
}
