package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康目标实体类
 */
@Data
@TableName("health_goal")
public class HealthGoal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String goalType;

    private String goalName;

    private Double targetValue;

    private Double currentValue;

    private String unit;

    private LocalDate startDate;

    private LocalDate endDate;

    /**
     * 状态：0进行中 1已完成 2已过期
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 完成进度百分比（非数据库字段，用于查询展示）
     */
    @TableField(exist = false)
    private Double progress;
}
