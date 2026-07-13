package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthWeight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HealthWeightMapper extends BaseMapper<HealthWeight> {

    @Select("SELECT * FROM health_weight WHERE user_id = #{userId} ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthWeight> selectRecent(@Param("userId") Long userId, @Param("limit") int limit);
}
