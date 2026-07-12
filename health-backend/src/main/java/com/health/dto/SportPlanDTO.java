package com.health.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 运动计划DTO
 */
@Data
public class SportPlanDTO {

    private Long id;

    private Long sportTypeId;

    private String planName;

    private String frequency;

    private Integer targetMinutes;

    private LocalDate startDate;

    private LocalDate endDate;
}
