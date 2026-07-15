package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_device_snapshot")
public class FamilyDeviceSnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberUserId;

    private String deviceName;

    private Boolean deviceOnline;

    private Boolean wearing;

    private Integer heartRate;

    private Integer oxygen;

    private Double temperature;

    private Integer sleepMinutes;

    private Integer steps;

    private LocalDateTime measuredAt;

    private LocalDateTime updatedAt;
}
