package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("diet_record")
public class DietRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private LocalDate recordDate;
    private String mealType;
    private Long foodId;
    private String foodName;
    private Double quantityG;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
