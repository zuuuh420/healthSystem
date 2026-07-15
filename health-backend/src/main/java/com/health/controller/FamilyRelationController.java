package com.health.controller;

import com.health.common.Result;
import com.health.dto.FamilyRelationCreateRequest;
import com.health.dto.FamilyRelationUpdateRequest;
import com.health.entity.FamilyRelation;
import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.service.FamilyRelationService;
import com.health.vo.FamilyRelationVO;
import com.health.vo.FamilyRelationRequestVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/family")
public class FamilyRelationController {

    private final FamilyRelationService familyRelationService;
    private final UserMapper userMapper;

    public FamilyRelationController(FamilyRelationService familyRelationService, UserMapper userMapper) {
        this.familyRelationService = familyRelationService;
        this.userMapper = userMapper;
    }

    @GetMapping("/invite-code")
    public Result<Map<String, String>> getInviteCode() {
        try {
            Map<String, String> data = new LinkedHashMap<>();
            data.put("inviteCode", familyRelationService.getCurrentInviteCode());
            return Result.success(data);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @GetMapping("/relations")
    public Result<List<FamilyRelationVO>> list() {
        try {
            List<FamilyRelationVO> result = familyRelationService.listActive().stream()
                    .map(this::toView)
                    .collect(Collectors.toList());
            return Result.success(result);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @PostMapping("/relations")
    public Result<FamilyRelationVO> create(@RequestBody FamilyRelationCreateRequest request) {
        try {
            return Result.success("申请已发送，等待对方确认", toView(familyRelationService.create(request)));
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @GetMapping("/requests")
    public Result<List<FamilyRelationRequestVO>> requests() {
        try {
            List<FamilyRelationRequestVO> result = familyRelationService.listPendingRequests().stream()
                    .map(this::toRequestView)
                    .collect(Collectors.toList());
            return Result.success(result);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @PatchMapping("/requests/{id}")
    public Result<Void> decide(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String decision = body == null ? null : body.get("decision");
            String relationship = body == null ? null : body.get("relationship");
            if (!familyRelationService.decideRequest(id, decision, relationship)) return Result.error("申请不存在、已处理或无权操作");
            return Result.success("ACCEPT".equalsIgnoreCase(decision) ? "关联申请已接受" : "关联申请已拒绝", null);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @DeleteMapping("/relations/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            if (!familyRelationService.delete(id)) return Result.error("关系不存在或无权操作");
            return Result.success("已解除关联", null);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @PatchMapping("/relations/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FamilyRelationUpdateRequest request) {
        try {
            if (!familyRelationService.update(id, request)) return Result.error("关系不存在或无权操作");
            return Result.success("关系已更新", null);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    private FamilyRelationVO toView(FamilyRelation relation) {
        FamilyRelationVO view = new FamilyRelationVO();
        view.setId(relation.getId());
        view.setMemberUserId(relation.getMemberUserId());
        view.setRelationship(relation.getRelationship());
        view.setStatus(relation.getStatus());
        view.setCreatedAt(relation.getCreatedAt());
        view.setUpdatedAt(relation.getUpdatedAt());
        User member = userMapper.selectById(relation.getMemberUserId());
        String originalName = member == null ? "已关联家人" : displayName(member);
        view.setMemberOriginalNickname(originalName);
        view.setMemberInviteCode(member == null ? null : member.getInviteCode());
        view.setMemberNickname(StringUtils.hasText(relation.getDisplayName()) ? relation.getDisplayName() : originalName);
        return view;
    }

    private String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) return user.getNickname();
        return user.getUsername();
    }

    private FamilyRelationRequestVO toRequestView(FamilyRelation relation) {
        FamilyRelationRequestVO view = new FamilyRelationRequestVO();
        view.setId(relation.getId());
        view.setRequesterUserId(relation.getOwnerUserId());
        User requester = userMapper.selectById(relation.getOwnerUserId());
        view.setRequesterNickname(requester == null ? "已注册用户" : displayName(requester));
        view.setRelationship(relation.getRelationship());
        view.setStatus(relation.getStatus());
        view.setCreatedAt(relation.getCreatedAt());
        return view;
    }
}
