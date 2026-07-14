package com.health.dto;

import lombok.Data;

@Data
public class FamilyRelationCreateRequest {
    private String inviteCode;
    private String relationship;
}
