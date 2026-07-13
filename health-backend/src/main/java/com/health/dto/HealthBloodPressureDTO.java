package com.health.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HealthBloodPressureDTO {
    @NotNull private Integer systolic;
    @NotNull private Integer diastolic;
    private Integer heartRateBpm;
    @NotNull private LocalDate recordDate;
    private LocalTime recordTime;
    private String note;
}
