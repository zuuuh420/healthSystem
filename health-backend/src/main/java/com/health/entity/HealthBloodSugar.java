package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("health_blood_sugar")
public class HealthBloodSugar {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private BigDecimal sugarLevel;

    private String measureType;

    private LocalDate recordDate;

    private LocalTime recordTime;

    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
