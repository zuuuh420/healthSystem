package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.SportPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 运动计划Mapper
 */
@Mapper
public interface SportPlanMapper extends BaseMapper<SportPlan> {

    /**
     * 查询运动计划（关联运动类型）
     */
    @Select("SELECT sp.*, st.name as sportTypeName " +
            "FROM sport_plan sp " +
            "LEFT JOIN sport_type st ON sp.sport_type_id = st.id " +
            "WHERE sp.user_id = #{userId} " +
            "ORDER BY sp.create_time DESC")
    List<SportPlan> selectWithSportType(@Param("userId") Long userId);
}
