package com.health.controller;

import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.service.SportStatsService;
import com.health.vo.SportStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 运动统计Controller
 */
@RestController
@RequestMapping("/api/sport-stats")
public class SportStatsController {

    @Autowired
    private SportStatsService sportStatsService;

    /**
     * 获取本周运动统计
     */
    @GetMapping("/weekly")
    public Result<SportStatsVO> getWeeklyStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        SportStatsVO stats = sportStatsService.getWeeklyStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取本月运动统计
     */
    @GetMapping("/monthly")
    public Result<SportStatsVO> getMonthlyStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        SportStatsVO stats = sportStatsService.getMonthlyStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取运动趋势数据
     */
    @GetMapping("/trend")
    public Result<SportStatsVO> getSportTrend(@RequestParam(defaultValue = "30") Integer days) {
        Long userId = SecurityUtil.getCurrentUserId();
        SportStatsVO stats = sportStatsService.getSportTrend(userId, days);
        return Result.success(stats);
    }
}
