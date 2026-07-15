package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.FamilyRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyRelationMapper extends BaseMapper<FamilyRelation> {

    @Select("SELECT id, owner_user_id, member_user_id, relationship, display_name, status, created_at, updated_at " +
            "FROM family_relation WHERE owner_user_id = #{ownerUserId} AND status = 'ACTIVE' ORDER BY created_at DESC")
    List<FamilyRelation> findByOwnerId(Long ownerUserId);

    @Select("SELECT id, owner_user_id, member_user_id, relationship, display_name, status, created_at, updated_at " +
            "FROM family_relation WHERE member_user_id = #{memberUserId} AND status = 'PENDING' ORDER BY created_at DESC")
    List<FamilyRelation> findPendingByMemberId(Long memberUserId);

    @Select("SELECT id FROM user WHERE invite_code = #{inviteCode} LIMIT 1")
    Long findUserIdByInviteCode(String inviteCode);
}
