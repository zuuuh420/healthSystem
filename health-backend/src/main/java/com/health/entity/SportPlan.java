package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 运动计划实体类
 */
@Data
@TableName("sport_plan")
public class SportPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sportTypeId;

    private String planName;

    private String frequency;

    private Integer targetMinutes;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 运动类型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String sportTypeName;
}
