package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.DietRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface DietRecordMapper extends BaseMapper<DietRecord> {

    @Select("SELECT COALESCE(SUM(calories), 0) FROM diet_record WHERE user_id = #{userId} AND record_date = #{date}")
    Double sumCaloriesByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT COALESCE(SUM(calories), 0) FROM diet_record WHERE user_id = #{userId} AND record_date BETWEEN #{start} AND #{end}")
    Double sumCaloriesBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT COALESCE(SUM(protein), 0) as protein, COALESCE(SUM(fat), 0) as fat, COALESCE(SUM(carbs), 0) as carbs FROM diet_record WHERE user_id = #{userId} AND record_date = #{date}")
    Map<String, Object> sumNutrientsByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT record_date as date, COALESCE(SUM(calories), 0) as calories FROM diet_record WHERE user_id = #{userId} AND record_date BETWEEN #{start} AND #{end} GROUP BY record_date ORDER BY record_date")
    List<Map<String, Object>> dailyCaloriesBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
