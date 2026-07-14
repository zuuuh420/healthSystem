package com.health;

import com.health.common.SecurityUtil;
import com.health.dto.FamilyRelationCreateRequest;
import com.health.entity.FamilyRelation;
import com.health.mapper.FamilyRelationMapper;
import com.health.service.FamilyRelationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class FamilyRelationServiceTest {

    @Autowired
    private FamilyRelationService familyRelationService;

    @Autowired
    private FamilyRelationMapper familyRelationMapper;

    @AfterEach
    void cleanUp() {
        familyRelationMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FamilyRelation>()
                        .eq("owner_user_id", 2L)
                        .eq("member_user_id", 3L));
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentUserGetsOwnInviteCode() {
        setUser(2L);

        assertThat(familyRelationService.getCurrentInviteCode()).matches("ZH[A-Z0-9]{10}");
    }

    @Test
    void selfAssociationIsRejected() {
        setUser(2L);
        String ownCode = familyRelationService.getCurrentInviteCode();
        FamilyRelationCreateRequest request = new FamilyRelationCreateRequest();
        request.setInviteCode(ownCode);
        request.setRelationship("本人");

        assertThatThrownBy(() -> familyRelationService.create(request))
                .hasMessageContaining("不能关联自己");
    }

    @Test
    void listAndRevokeAreScopedToOwner() {
        setUser(2L);
        String memberCode = familyRelationService.getInviteCodeForUser(3L);
        FamilyRelationCreateRequest request = new FamilyRelationCreateRequest();
        request.setInviteCode(memberCode);
        request.setRelationship("父亲");
        FamilyRelation created = familyRelationService.create(request);
        assertThat(created.getId()).isNotNull();

        setUser(3L);
        assertThat(familyRelationService.delete(created.getId())).isFalse();
    }

    private void setUser(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        authentication.setDetails("testuser:user");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
