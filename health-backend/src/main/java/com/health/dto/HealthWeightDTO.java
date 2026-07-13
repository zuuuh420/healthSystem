package com.health.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthWeightDTO {
    @NotNull private BigDecimal weight;
    @NotNull private BigDecimal heightCm;
    @NotNull private LocalDate recordDate;
    private String note;
}
