package com.health.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DietRecordDTO {
    private LocalDate recordDate;
    private String mealType;
    private Long foodId;
    private Double quantityG;
    private String note;
}
