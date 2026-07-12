package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
    @Select("SELECT DISTINCT category FROM food WHERE category IS NOT NULL AND category != '' ORDER BY category")
    List<String> selectCategories();
}
