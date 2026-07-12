package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private LocalDate recordDate;
    private Double weight;
    private Double bmi;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Double bloodSugar;
    private Integer heartRate;
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
