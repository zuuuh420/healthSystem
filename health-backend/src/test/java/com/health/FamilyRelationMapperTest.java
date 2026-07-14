package com.health;

import com.health.entity.FamilyRelation;
import com.health.mapper.FamilyRelationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FamilyRelationMapperTest {

    @Autowired
    private FamilyRelationMapper familyRelationMapper;

    @Test
    void findByOwnerIdReturnsOnlyActiveRelations() {
        List<FamilyRelation> relations = familyRelationMapper.findByOwnerId(2L);

        assertThat(relations).allMatch(relation ->
                Long.valueOf(2L).equals(relation.getOwnerUserId())
                        && "ACTIVE".equals(relation.getStatus()));
    }

    @Test
    void findUserByInviteCodeReturnsOnlyMatchingUser() {
        assertThat(familyRelationMapper.findUserIdByInviteCode("not-a-real-code")).isNull();
    }
}
