package com.health.service;

import com.health.dto.FamilyRelationCreateRequest;
import com.health.dto.FamilyRelationUpdateRequest;
import com.health.entity.FamilyRelation;

import java.util.List;

public interface FamilyRelationService {
    String getCurrentInviteCode();

    String getInviteCodeForUser(Long userId);

    List<FamilyRelation> listActive();

    List<FamilyRelation> listPendingRequests();

    FamilyRelation create(FamilyRelationCreateRequest request);

    boolean delete(Long id);

    boolean update(Long id, FamilyRelationUpdateRequest request);

    boolean decideRequest(Long id, String decision);

    boolean decideRequest(Long id, String decision, String relationship);
}
