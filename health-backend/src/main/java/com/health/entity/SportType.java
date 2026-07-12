package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 运动类型实体类
 */
@Data
@TableName("sport_type")
public class SportType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String category;

    private Double caloriesPerMinute;

    private String icon;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
