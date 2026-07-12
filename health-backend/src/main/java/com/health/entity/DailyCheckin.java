package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日打卡实体类
 */
@Data
@TableName("daily_checkin")
public class DailyCheckin {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate checkinDate;

    private Long sportRecordId;

    private String checkinType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
