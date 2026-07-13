package com.health.controller;

import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.entity.HealthRecord;
import com.health.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping
    public Result<HealthRecord> create(@RequestBody HealthRecord record) {
        Long userId = SecurityUtil.getCurrentUserId();
        record.setUserId(userId);
        boolean success = healthRecordService.addHealthRecord(record);
        if (success) return Result.success("记录成功", record);
        return Result.error("记录失败");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HealthRecord record) {
        Long userId = SecurityUtil.getCurrentUserId();
        record.setId(id);
        boolean success = healthRecordService.updateHealthRecord(record, userId);
        if (success) return Result.success("修改成功", null);
        return Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = healthRecordService.deleteHealthRecord(id, userId);
        if (success) return Result.success("删除成功", null);
        return Result.error("删除失败");
    }

    @GetMapping
    public Result<List<HealthRecord>> list() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(healthRecordService.listByUser(userId));
    }

    @GetMapping("/range")
    public Result<List<HealthRecord>> listByRange(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(healthRecordService.listByDateRange(userId, start, end));
    }
}
