package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.DailyCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日打卡Mapper
 */
@Mapper
public interface DailyCheckinMapper extends BaseMapper<DailyCheckin> {

    /**
     * 查询用户某月的打卡日期列表
     */
    @Select("SELECT checkin_date FROM daily_checkin " +
            "WHERE user_id = #{userId} " +
            "AND checkin_type = #{checkinType} " +
            "AND YEAR(checkin_date) = #{year} " +
            "AND MONTH(checkin_date) = #{month} " +
            "ORDER BY checkin_date")
    List<LocalDate> selectCheckinDates(@Param("userId") Long userId,
                                       @Param("checkinType") String checkinType,
                                       @Param("year") int year,
                                       @Param("month") int month);

    /**
     * 查询用户连续打卡天数
     */
    @Select("SELECT COUNT(*) FROM (" +
            "SELECT checkin_date FROM daily_checkin " +
            "WHERE user_id = #{userId} AND checkin_type = #{checkinType} " +
            "ORDER BY checkin_date DESC " +
            "LIMIT 365" +
            ") t")
    int countTotalCheckins(@Param("userId") Long userId, @Param("checkinType") String checkinType);
}
