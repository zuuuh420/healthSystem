package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.entity.SportType;
import com.health.service.SportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 运动类型Controller
 */
@RestController
@RequestMapping("/api/sport-types")
public class SportTypeController {

    @Autowired
    private SportTypeService sportTypeService;

    /**
     * 获取运动类型列表
     */
    @GetMapping
    public Result<IPage<SportType>> getSportTypes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        IPage<SportType> page = sportTypeService.getSportTypes(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    /**
     * 获取运动类型详情
     */
    @GetMapping("/{id}")
    public Result<SportType> getSportTypeById(@PathVariable Long id) {
        SportType sportType = sportTypeService.getSportTypeById(id);
        if (sportType == null) {
            return Result.error("运动类型不存在");
        }
        return Result.success(sportType);
    }

    /**
     * 新增运动类型
     */
    @PostMapping
    public Result<SportType> addSportType(@RequestBody SportType sportType) {
        boolean success = sportTypeService.addSportType(sportType);
        if (success) {
            return Result.success("新增成功", sportType);
        }
        return Result.error("新增失败");
    }

    /**
     * 修改运动类型
     */
    @PutMapping("/{id}")
    public Result<Void> updateSportType(@PathVariable Long id, @RequestBody SportType sportType) {
        sportType.setId(id);
        boolean success = sportTypeService.updateSportType(sportType);
        if (success) {
            return Result.success("修改成功", null);
        }
        return Result.error("修改失败");
    }

    /**
     * 删除运动类型
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSportType(@PathVariable Long id) {
        boolean success = sportTypeService.deleteSportType(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }
}
