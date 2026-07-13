package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthBloodPressure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HealthBloodPressureMapper extends BaseMapper<HealthBloodPressure> {

    @Select("SELECT * FROM health_blood_pressure WHERE user_id = #{userId} ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthBloodPressure> selectRecent(@Param("userId") Long userId, @Param("limit") int limit);
}
