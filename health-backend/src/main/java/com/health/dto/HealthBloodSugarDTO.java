package com.health.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HealthBloodSugarDTO {
    @NotNull private BigDecimal sugarLevel;
    @NotNull private String measureType;
    @NotNull private LocalDate recordDate;
    private LocalTime recordTime;
    private String note;
}
