package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 运动记录实体类
 */
@Data
@TableName("sport_record")
public class SportRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sportTypeId;

    private Integer durationMinutes;

    private Double caloriesBurned;

    private LocalDate sportDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 运动类型名称（非数据库字段，用于查询展示）
     */
    @TableField(exist = false)
    private String sportTypeName;

    /**
     * 运动分类（非数据库字段，用于查询展示）
     */
    @TableField(exist = false)
    private String sportCategory;
}
