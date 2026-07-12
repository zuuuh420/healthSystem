package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthGoal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康目标Mapper
 */
@Mapper
public interface HealthGoalMapper extends BaseMapper<HealthGoal> {
}
