package com.health;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.common.Result;
import com.health.controller.FamilyHealthSnapshotController;
import com.health.entity.FamilyDeviceSnapshot;
import com.health.entity.FamilyRelation;
import com.health.mapper.FamilyDeviceSnapshotMapper;
import com.health.mapper.FamilyRelationMapper;
import com.health.vo.FamilyHealthSnapshotVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FamilyHealthSnapshotControllerTest {

    @Autowired
    private FamilyHealthSnapshotController controller;

    @Autowired
    private FamilyRelationMapper relationMapper;

    @Autowired
    private FamilyDeviceSnapshotMapper snapshotMapper;

    @AfterEach
    void cleanUp() {
        relationMapper.delete(new QueryWrapper<FamilyRelation>()
                .in("owner_user_id", 2L, 3L)
                .in("member_user_id", 2L, 3L));
        SecurityContextHolder.clearContext();
    }

    @Test
    void authorizedWearingMemberReceivesMetrics() {
        ensureSnapshot(2L, true);
        createRelation(3L, 2L);
        setUser(3L);

        Result<List<FamilyHealthSnapshotVO>> result = controller.list();

        assertThat(result.getCode()).isEqualTo(200);
        FamilyHealthSnapshotVO snapshot = result.getData().stream()
                .filter(item -> item.getMemberUserId().equals(2L))
                .findFirst().orElseThrow(AssertionError::new);
        assertThat(snapshot.getWearing()).isTrue();
        assertThat(snapshot.getHeartRate()).isEqualTo(77);
        assertThat(snapshot.getSteps()).isEqualTo(6842);
    }

    @Test
    void authorizedNotWearingMemberHidesMetrics() {
        ensureSnapshot(3L, false);
        createRelation(2L, 3L);
        setUser(2L);

        Result<List<FamilyHealthSnapshotVO>> result = controller.list();

        FamilyHealthSnapshotVO snapshot = result.getData().stream()
                .filter(item -> item.getMemberUserId().equals(3L))
                .findFirst().orElseThrow(AssertionError::new);
        assertThat(snapshot.getWearing()).isFalse();
        assertThat(snapshot.getHeartRate()).isNull();
        assertThat(snapshot.getOxygen()).isNull();
        assertThat(snapshot.getTemperature()).isNull();
        assertThat(snapshot.getSleepMinutes()).isNull();
        assertThat(snapshot.getSteps()).isNull();
    }

    @Test
    void unlinkedMemberIsNotReturned() {
        setUser(2L);

        Result<List<FamilyHealthSnapshotVO>> result = controller.list();

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isEmpty();
    }

    private void createRelation(Long ownerId, Long memberId) {
        relationMapper.delete(new QueryWrapper<FamilyRelation>()
                .eq("owner_user_id", ownerId).eq("member_user_id", memberId));
        FamilyRelation relation = new FamilyRelation();
        relation.setOwnerUserId(ownerId);
        relation.setMemberUserId(memberId);
        relation.setRelationship("家人");
        relation.setStatus("ACTIVE");
        relationMapper.insert(relation);
    }

    private void ensureSnapshot(Long memberId, boolean wearing) {
        snapshotMapper.delete(new QueryWrapper<FamilyDeviceSnapshot>().eq("member_user_id", memberId));
        FamilyDeviceSnapshot snapshot = new FamilyDeviceSnapshot();
        snapshot.setMemberUserId(memberId);
        snapshot.setDeviceName("知衡 Band 2");
        snapshot.setDeviceOnline(true);
        snapshot.setWearing(wearing);
        if (wearing) {
            snapshot.setHeartRate(77);
            snapshot.setOxygen(99);
            snapshot.setTemperature(36.8);
            snapshot.setSleepMinutes(438);
            snapshot.setSteps(6842);
        }
        snapshotMapper.insert(snapshot);
    }

    private void setUser(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        authentication.setDetails("testuser:user");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
