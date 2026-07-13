package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.entity.Food;
import com.health.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public Result<IPage<Food>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(foodService.page(keyword, category, page, size));
    }

    @GetMapping("/all")
    public Result<List<Food>> listAll() {
        return Result.success(foodService.listAll());
    }

    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(foodService.listCategories());
    }

    @GetMapping("/{id}")
    public Result<Food> getById(@PathVariable Long id) {
        Food food = foodService.getById(id);
        if (food == null) return Result.error("食物不存在");
        return Result.success(food);
    }

    @PostMapping
    public Result<Food> create(@RequestBody Food food) {
        SecurityUtil.requireAdmin();
        return Result.success(foodService.create(food));
    }

    @PutMapping("/{id}")
    public Result<Food> update(@PathVariable Long id, @RequestBody Food food) {
        SecurityUtil.requireAdmin();
        Food updated = foodService.update(id, food);
        if (updated == null) return Result.error("食物不存在");
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        SecurityUtil.requireAdmin();
        foodService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/batch")
    public Result<Void> batchImport(@RequestBody List<Food> foods) {
        SecurityUtil.requireAdmin();
        foodService.batchImport(foods);
        return Result.success("导入成功", null);
    }
}
