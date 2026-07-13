package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthHeartRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HealthHeartRateMapper extends BaseMapper<HealthHeartRate> {

    @Select("SELECT * FROM health_heart_rate WHERE user_id = #{userId} ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthHeartRate> selectRecent(@Param("userId") Long userId, @Param("limit") int limit);
}
