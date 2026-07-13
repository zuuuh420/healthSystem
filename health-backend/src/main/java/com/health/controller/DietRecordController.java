package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.CalorieStatsDTO;
import com.health.dto.DietRecordDTO;
import com.health.entity.DietRecord;
import com.health.service.DietRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/diet-records")
public class DietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    @PostMapping
    public Result<DietRecord> create(@RequestBody DietRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        DietRecord record = dietRecordService.create(userId, dto);
        if (record == null) return Result.error("食物不存在");
        return Result.success("记录成功", record);
    }

    @PutMapping("/{id}")
    public Result<DietRecord> update(@PathVariable Long id, @RequestBody DietRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        DietRecord record = dietRecordService.update(id, userId, dto);
        if (record == null) return Result.error("更新失败，请检查记录是否存在或食物是否有效");
        return Result.success("修改成功", record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        dietRecordService.delete(id, userId);
        return Result.success("删除成功", null);
    }

    @GetMapping
    public Result<IPage<DietRecord>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(dietRecordService.page(userId, date, page, size));
    }

    @GetMapping("/range")
    public Result<List<DietRecord>> listByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(dietRecordService.listByRange(userId, start, end));
    }

    @GetMapping("/stats")
    public Result<CalorieStatsDTO> stats() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(dietRecordService.getCalorieStats(userId));
    }
}
