package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.springframework.transaction.annotation.Transactional;
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
    public List<FamilyRelation> listPendingRequests() {
        return baseMapper.findPendingByMemberId(requireCurrentUserId());
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
                ? request.getRelationship().trim() : "";
        if (relationship.length() > 32) {
            throw new IllegalArgumentException("称谓不能超过32个字符");
        }

        FamilyRelation reverseActive = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, memberId)
                .eq(FamilyRelation::getMemberUserId, ownerId)
                .eq(FamilyRelation::getStatus, "ACTIVE"), false);
        if (reverseActive != null) throw new IllegalStateException("该家人已经关联");

        FamilyRelation existing = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, ownerId)
                .eq(FamilyRelation::getMemberUserId, memberId), false);
        if (existing != null) {
            if ("ACTIVE".equals(existing.getStatus())) {
                throw new IllegalStateException("该家人已经关联");
            }
            if ("PENDING".equals(existing.getStatus())) {
                throw new IllegalStateException("关联申请已发送，请等待对方确认");
            }
            existing.setRelationship(relationship);
            existing.setStatus("PENDING");
            updateById(existing);
            return existing;
        }

        FamilyRelation relation = new FamilyRelation();
        relation.setOwnerUserId(ownerId);
        relation.setMemberUserId(memberId);
        relation.setRelationship(relationship);
        relation.setStatus("PENDING");
        save(relation);
        return relation;
    }

    @Override
    @Transactional
    public boolean decideRequest(Long id, String decision) {
        return decideRequest(id, decision, "");
    }

    @Override
    @Transactional
    public boolean decideRequest(Long id, String decision, String relationship) {
        Long currentUserId = requireCurrentUserId();
        FamilyRelation request = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getId, id)
                .eq(FamilyRelation::getMemberUserId, currentUserId)
                .eq(FamilyRelation::getStatus, "PENDING"), false);
        if (request == null) return false;

        String normalized = decision == null ? "" : decision.trim().toUpperCase();
        if ("REJECT".equals(normalized)) {
            request.setStatus("REJECTED");
            return updateById(request);
        }
        if (!"ACCEPT".equals(normalized)) throw new IllegalArgumentException("申请处理方式无效");

        String reverseRelationship = StringUtils.hasText(relationship) ? relationship.trim() : "";
        if (reverseRelationship.length() > 32) throw new IllegalArgumentException("称谓不能超过32个字符");

        request.setStatus("ACTIVE");
        if (!updateById(request)) return false;

        FamilyRelation reverse = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, currentUserId)
                .eq(FamilyRelation::getMemberUserId, request.getOwnerUserId()), false);
        if (reverse == null) {
            reverse = new FamilyRelation();
            reverse.setOwnerUserId(currentUserId);
            reverse.setMemberUserId(request.getOwnerUserId());
            reverse.setRelationship(reverseRelationship);
            reverse.setStatus("ACTIVE");
            save(reverse);
        } else {
            reverse.setRelationship(reverseRelationship);
            reverse.setStatus("ACTIVE");
            updateById(reverse);
        }
        return true;
    }

    @Override
    public boolean delete(Long id) {
        FamilyRelation relation = findOwnedRelation(id);
        if (relation == null) return false;
        relation.setStatus("REVOKED");
        boolean updated = updateById(relation);
        if (!updated) return false;
        FamilyRelation reverse = getOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, relation.getMemberUserId())
                .eq(FamilyRelation::getMemberUserId, relation.getOwnerUserId())
                .in(FamilyRelation::getStatus, "ACTIVE", "PENDING"), false);
        if (reverse != null) {
            reverse.setStatus("REVOKED");
            updateById(reverse);
        }
        return true;
    }

    @Override
    public boolean update(Long id, FamilyRelationUpdateRequest request) {
        FamilyRelation relation = findOwnedRelation(id);
        if (relation == null || request == null) return false;
        boolean clearDisplayName = false;
        if (request.getRelationship() != null) {
            String relationship = request.getRelationship().trim();
            if (relationship.length() > 32) throw new IllegalArgumentException("称谓不能超过32个字符");
            relation.setRelationship(relationship);
        }
        if (request.getDisplayName() != null) {
            String displayName = request.getDisplayName().trim();
            if (displayName.length() > 64) throw new IllegalArgumentException("备注名不能超过64个字符");
            relation.setDisplayName(displayName.isEmpty() ? null : displayName);
            clearDisplayName = displayName.isEmpty();
        }
        if (StringUtils.hasText(request.getStatus())) {
            String status = request.getStatus().trim().toUpperCase();
            if (!"ACTIVE".equals(status) && !"REVOKED".equals(status)) {
                throw new IllegalArgumentException("关系状态无效");
            }
            relation.setStatus(status);
        }
        boolean updated = updateById(relation);
        if (updated && clearDisplayName) {
            baseMapper.update(null, new UpdateWrapper<FamilyRelation>()
                    .eq("id", relation.getId())
                    .set("display_name", null));
        }
        return updated;
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
