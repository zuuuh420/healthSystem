package com.health.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyRelationRequestVO {
    private Long id;
    private Long requesterUserId;
    private String requesterNickname;
    private String relationship;
    private String status;
    private LocalDateTime createdAt;
}
