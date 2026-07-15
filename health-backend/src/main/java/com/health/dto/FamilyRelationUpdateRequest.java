package com.health.dto;

import lombok.Data;

@Data
public class FamilyRelationUpdateRequest {
    private String relationship;
    private String displayName;
    private String status;
}
