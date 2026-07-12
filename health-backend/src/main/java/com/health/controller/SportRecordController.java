package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.SportRecordDTO;
import com.health.entity.SportRecord;
import com.health.service.SportRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录Controller
 */
@RestController
@RequestMapping("/sport-records")
public class SportRecordController {

    @Autowired
    private SportRecordService sportRecordService;

    /**
     * 获取运动记录列表
     */
    @GetMapping
    public Result<IPage<SportRecord>> getSportRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long sportTypeId) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<SportRecord> page = sportRecordService.getSportRecords(userId, pageNum, pageSize, startDate, endDate, sportTypeId);
        return Result.success(page);
    }

    /**
     * 获取运动记录详情
     */
    @GetMapping("/{id}")
    public Result<SportRecord> getSportRecordById(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        SportRecord record = sportRecordService.getSportRecordById(id, userId);
        if (record == null) {
            return Result.error("运动记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 新增运动记录
     */
    @PostMapping
    public Result<SportRecord> addSportRecord(@RequestBody SportRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        SportRecord record = new SportRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);

        boolean success = sportRecordService.addSportRecord(record);
        if (success) {
            return Result.success("新增成功", record);
        }
        return Result.error("新增失败");
    }

    /**
     * 修改运动记录
     */
    @PutMapping("/{id}")
    public Result<Void> updateSportRecord(@PathVariable Long id, @RequestBody SportRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        SportRecord record = new SportRecord();
        BeanUtils.copyProperties(dto, record);
        record.setId(id);

        boolean success = sportRecordService.updateSportRecord(record, userId);
        if (success) {
            return Result.success("修改成功", null);
        }
        return Result.error("修改失败");
    }

    /**
     * 删除运动记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSportRecord(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean success = sportRecordService.deleteSportRecord(id, userId);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    /**
     * 获取今日运动记录
     */
    @GetMapping("/today")
    public Result<List<SportRecord>> getTodayRecords() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<SportRecord> records = sportRecordService.getTodayRecords(userId);
        return Result.success(records);
    }
}
