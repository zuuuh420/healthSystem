package com.health.service;

import com.health.common.SecurityUtil;
import com.health.entity.FamilyDeviceSnapshot;
import com.health.entity.FamilyRelation;
import com.health.entity.User;
import com.health.mapper.FamilyDeviceSnapshotMapper;
import com.health.mapper.FamilyRelationMapper;
import com.health.mapper.UserMapper;
import com.health.vo.FamilyHealthSnapshotVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FamilyHealthSnapshotService {

    private final FamilyRelationMapper relationMapper;
    private final FamilyDeviceSnapshotMapper snapshotMapper;
    private final UserMapper userMapper;

    public FamilyHealthSnapshotService(FamilyRelationMapper relationMapper,
                                       FamilyDeviceSnapshotMapper snapshotMapper,
                                       UserMapper userMapper) {
        this.relationMapper = relationMapper;
        this.snapshotMapper = snapshotMapper;
        this.userMapper = userMapper;
    }

    public List<FamilyHealthSnapshotVO> listAuthorizedSnapshots() {
        Long ownerId = SecurityUtil.getCurrentUserId();
        if (ownerId == null) throw new IllegalStateException("请先登录");

        List<FamilyRelation> relations = relationMapper.findByOwnerId(ownerId);
        if (relations.isEmpty()) return Collections.emptyList();
        List<Long> memberIds = relations.stream().map(FamilyRelation::getMemberUserId).collect(Collectors.toList());
        Map<Long, FamilyDeviceSnapshot> snapshots = snapshotMapper.findByMemberIds(memberIds).stream()
                .collect(Collectors.toMap(FamilyDeviceSnapshot::getMemberUserId, Function.identity()));

        return relations.stream().map(relation -> toView(relation, snapshots.get(relation.getMemberUserId()))).collect(Collectors.toList());
    }

    private FamilyHealthSnapshotVO toView(FamilyRelation relation, FamilyDeviceSnapshot snapshot) {
        FamilyHealthSnapshotVO view = new FamilyHealthSnapshotVO();
        view.setRelationId(relation.getId());
        view.setMemberUserId(relation.getMemberUserId());
        view.setRelationship(relation.getRelationship());
        User member = userMapper.selectById(relation.getMemberUserId());
        String originalName = member == null ? "已关联家人" : displayName(member);
        view.setMemberOriginalNickname(originalName);
        view.setMemberInviteCode(member == null ? null : member.getInviteCode());
        view.setMemberNickname(StringUtils.hasText(relation.getDisplayName()) ? relation.getDisplayName() : originalName);
        if (snapshot == null) {
            view.setDeviceName("知衡 Band 2");
            view.setDeviceOnline(false);
            view.setWearing(false);
            return view;
        }
        view.setDeviceName(snapshot.getDeviceName());
        view.setDeviceOnline(snapshot.getDeviceOnline());
        view.setWearing(snapshot.getWearing());
        view.setMeasuredAt(snapshot.getMeasuredAt());
        if (Boolean.TRUE.equals(snapshot.getDeviceOnline()) && Boolean.TRUE.equals(snapshot.getWearing())) {
            view.setHeartRate(snapshot.getHeartRate());
            view.setOxygen(snapshot.getOxygen());
            view.setTemperature(snapshot.getTemperature());
            view.setSleepMinutes(snapshot.getSleepMinutes());
            view.setSteps(snapshot.getSteps());
        }
        return view;
    }

    private String displayName(User user) {
        return user.getNickname() == null || user.getNickname().trim().isEmpty()
                ? user.getUsername() : user.getNickname();
    }
}
