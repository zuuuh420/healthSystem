package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.FamilyDeviceSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyDeviceSnapshotMapper extends BaseMapper<FamilyDeviceSnapshot> {

    @Select({
            "<script>",
            "SELECT id, member_user_id, device_name, device_online, wearing, heart_rate, oxygen, temperature, sleep_minutes, steps, measured_at, updated_at",
            "FROM family_device_snapshot WHERE member_user_id IN",
            "<foreach collection='memberIds' item='memberId' open='(' separator=',' close=')'>#{memberId}</foreach>",
            "</script>"
    })
    List<FamilyDeviceSnapshot> findByMemberIds(List<Long> memberIds);
}
