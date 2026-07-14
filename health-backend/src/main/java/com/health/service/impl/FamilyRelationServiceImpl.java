package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.common.SecurityUtil;
import com.health.dto.FamilyRelationCreateRequest;
import com.health.dto.FamilyRelationUpdateRequest;
import com.health.entity.FamilyRelation;
import com.health.entity.User;
import com.health.mapper.FamilyRelationMapper;
import com.health.mapper.UserMapper;
import com.health.service.FamilyRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FamilyRelationServiceImpl extends ServiceImpl<FamilyRelationMapper, FamilyRelation>
        implements FamilyRelationService {

    private final UserMapper userMapper;

    public FamilyRelationServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String getCurrentInviteCode() {
        Long userId = requireCurrentUserId();
        return getInviteCodeForUser(userId);
    }

    @Override
    public String getInviteCodeForUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || !StringUtils.hasText(user.getInviteCode())) {
            throw new IllegalStateException("用户关联码不可用");
        }
        return user.getInviteCode();
    }

    @Override
    public List<FamilyRelation> listActive() {
        return baseMapper.findByOwnerId(requireCurrentUserId());
    }

    @Override
    public FamilyRelation create(FamilyRelationCreateRequest request) {
        Long ownerId = requireCurrentUserId();
        if (request == null || !StringUtils.hasText(request.getInviteCode())) {
            throw new IllegalArgumentException("关联码不能为空");
        }
        Long memberId = baseMapper.findUserIdByInviteCode(request.getInviteCode().trim().toUpperCase());
        if (memberId == null) {
            throw new IllegalArgumentException("关联码无效");
        }
        if (ownerId.equals(memberId)) {
            throw new IllegalArgumentException("不能关联自己");
        }
        String relationship = StringUtils.hasText(request.getRelationship())
                ? request.getRelationship().trim() : "家人";
        if (relationship.length() > 32) {
            throw new IllegalArgumentException("称谓不能超过32个字符");
        }

        FamilyRelation existing = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, ownerId)
                .eq(FamilyRelation::getMemberUserId, memberId), false);
        if (existing != null) {
            if ("ACTIVE".equals(existing.getStatus())) {
                throw new IllegalStateException("该家人已经关联");
            }
            existing.setRelationship(relationship);
            existing.setStatus("ACTIVE");
            updateById(existing);
            return existing;
        }

        FamilyRelation relation = new FamilyRelation();
        relation.setOwnerUserId(ownerId);
        relation.setMemberUserId(memberId);
        relation.setRelationship(relationship);
        relation.setStatus("ACTIVE");
        save(relation);
        return relation;
    }

    @Override
    public boolean delete(Long id) {
        FamilyRelation relation = findOwnedRelation(id);
        if (relation == null) return false;
        relation.setStatus("REVOKED");
        return updateById(relation);
    }

    @Override
    public boolean update(Long id, FamilyRelationUpdateRequest request) {
        FamilyRelation relation = findOwnedRelation(id);
        if (relation == null || request == null) return false;
        if (StringUtils.hasText(request.getRelationship())) {
            String relationship = request.getRelationship().trim();
            if (relationship.length() > 32) throw new IllegalArgumentException("称谓不能超过32个字符");
            relation.setRelationship(relationship);
        }
        if (StringUtils.hasText(request.getStatus())) {
            String status = request.getStatus().trim().toUpperCase();
            if (!"ACTIVE".equals(status) && !"REVOKED".equals(status)) {
                throw new IllegalArgumentException("关系状态无效");
            }
            relation.setStatus(status);
        }
        return updateById(relation);
    }

    private FamilyRelation findOwnedRelation(Long id) {
        if (id == null) return null;
        return getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getId, id)
                .eq(FamilyRelation::getOwnerUserId, requireCurrentUserId()), false);
    }

    private Long requireCurrentUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new IllegalStateException("请先登录");
        return userId;
    }
}
