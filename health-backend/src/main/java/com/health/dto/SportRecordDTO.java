package com.health.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 运动记录DTO
 */
@Data
public class SportRecordDTO {

    private Long id;

    private Long sportTypeId;

    private Integer durationMinutes;

    private LocalDate sportDate;

    private String remark;
}
