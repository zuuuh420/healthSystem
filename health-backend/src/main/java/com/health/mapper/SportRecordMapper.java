package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.SportRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录Mapper
 */
@Mapper
public interface SportRecordMapper extends BaseMapper<SportRecord> {

    /**
     * 查询运动记录（关联运动类型）
     */
    @Select("SELECT sr.*, st.name as sportTypeName, st.category as sportCategory " +
            "FROM sport_record sr " +
            "LEFT JOIN sport_type st ON sr.sport_type_id = st.id " +
            "WHERE sr.user_id = #{userId} " +
            "ORDER BY sr.sport_date DESC, sr.create_time DESC")
    List<SportRecord> selectWithSportType(@Param("userId") Long userId);

    /**
     * 查询用户今日运动记录
     */
    @Select("SELECT sr.*, st.name as sportTypeName, st.category as sportCategory " +
            "FROM sport_record sr " +
            "LEFT JOIN sport_type st ON sr.sport_type_id = st.id " +
            "WHERE sr.user_id = #{userId} AND sr.sport_date = #{today} " +
            "ORDER BY sr.create_time DESC")
    List<SportRecord> selectTodayRecords(@Param("userId") Long userId, @Param("today") LocalDate today);
}
