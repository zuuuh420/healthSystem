package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthBloodSugar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HealthBloodSugarMapper extends BaseMapper<HealthBloodSugar> {

    @Select("SELECT * FROM health_blood_sugar WHERE user_id = #{userId} ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthBloodSugar> selectRecent(@Param("userId") Long userId, @Param("limit") int limit);
}
