package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("food")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String category;
    @TableField("calories_per_100g")
    private Double caloriesPer100g;
    @TableField("protein_per_100g")
    private Double proteinPer100g;
    @TableField("fat_per_100g")
    private Double fatPer100g;
    @TableField("carbs_per_100g")
    private Double carbsPer100g;
    private String unit;
    private String imageUrl;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
