package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.entity.Food;
import java.util.List;

public interface FoodService {
    IPage<Food> page(String keyword, String category, int page, int size);
    List<Food> listAll();
    List<String> listCategories();
    Food getById(Long id);
    Food create(Food food);
    Food update(Long id, Food food);
    void delete(Long id);
    void batchImport(List<Food> foods);
}
