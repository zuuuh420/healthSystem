package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.Food;
import com.health.mapper.FoodMapper;
import com.health.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public IPage<Food> page(String keyword, String category, int page, int size) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Food::getName, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Food::getCategory, category);
        }
        wrapper.orderByDesc(Food::getId);
        return baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Food> listAll() {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Food::getCategory).orderByAsc(Food::getName);
        return list(wrapper);
    }

    @Override
    public List<String> listCategories() {
        return baseMapper.selectCategories();
    }

    @Override
    public Food getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Food create(Food food) {
        save(food);
        return food;
    }

    @Override
    public Food update(Long id, Food food) {
        food.setId(id);
        updateById(food);
        return food;
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public void batchImport(List<Food> foods) {
        saveBatch(foods);
    }
}
