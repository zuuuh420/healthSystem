package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("health_blood_pressure")
public class HealthBloodPressure {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer systolic;

    private Integer diastolic;

    private Integer heartRateBpm;

    private LocalDate recordDate;

    private LocalTime recordTime;

    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
