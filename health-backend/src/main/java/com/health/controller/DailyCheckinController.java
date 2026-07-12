package com.health.controller;

import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.service.DailyCheckinService;
import com.health.vo.CheckinVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 每日打卡Controller
 */
@RestController
@RequestMapping("/checkin")
public class DailyCheckinController {

    @Autowired
    private DailyCheckinService dailyCheckinService;

    /**
     * 今日打卡
     */
    @PostMapping
    public Result<Void> checkin(
            @RequestParam(required = false) Long sportRecordId,
            @RequestParam(defaultValue = "sport") String checkinType) {
        Long userId = SecurityUtil.getCurrentUserId();

        boolean success = dailyCheckinService.checkin(userId, sportRecordId, checkinType);
        if (success) {
            return Result.success("打卡成功", null);
        }
        return Result.error("今日已打卡");
    }

    /**
     * 查询今日是否已打卡
     */
    @GetMapping("/today")
    public Result<CheckinVO> getTodayCheckinStatus(
            @RequestParam(defaultValue = "sport") String checkinType) {
        Long userId = SecurityUtil.getCurrentUserId();
        CheckinVO vo = dailyCheckinService.getTodayCheckinStatus(userId, checkinType);
        return Result.success(vo);
    }

    /**
     * 查询连续打卡天数
     */
    @GetMapping("/streak")
    public Result<CheckinVO> getCheckinStreak(
            @RequestParam(defaultValue = "sport") String checkinType) {
        Long userId = SecurityUtil.getCurrentUserId();
        CheckinVO vo = dailyCheckinService.getCheckinStreak(userId, checkinType);
        return Result.success(vo);
    }

    /**
     * 查询某月打卡日历
     */
    @GetMapping("/calendar")
    public Result<CheckinVO> getCheckinCalendar(
            @RequestParam String month,
            @RequestParam(defaultValue = "sport") String checkinType) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 解析月份参数
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthNum = Integer.parseInt(parts[1]);

        CheckinVO vo = dailyCheckinService.getCheckinCalendar(userId, checkinType, year, monthNum);
        return Result.success(vo);
    }
}
