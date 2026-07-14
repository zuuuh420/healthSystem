package com.health.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyRelationVO {
    private Long id;
    private Long memberUserId;
    private String memberNickname;
    private String relationship;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
