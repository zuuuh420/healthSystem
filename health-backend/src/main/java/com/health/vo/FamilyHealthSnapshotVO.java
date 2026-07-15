package com.health.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyHealthSnapshotVO {
    private Long relationId;
    private Long memberUserId;
    private String memberNickname;
    private String memberOriginalNickname;
    private String memberInviteCode;
    private String relationship;
    private String deviceName;
    private Boolean deviceOnline;
    private Boolean wearing;
    private Integer heartRate;
    private Integer oxygen;
    private Double temperature;
    private Integer sleepMinutes;
    private Integer steps;
    private LocalDateTime measuredAt;
}
