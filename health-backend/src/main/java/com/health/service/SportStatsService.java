package com.health.service;

import com.health.vo.SportStatsVO;

/**
 * 运动统计Service接口
 */
public interface SportStatsService {

    /**
     * 获取本周运动统计
     */
    SportStatsVO getWeeklyStats(Long userId);

    /**
     * 获取本月运动统计
     */
    SportStatsVO getMonthlyStats(Long userId);

    /**
     * 获取运动趋势数据
     */
    SportStatsVO getSportTrend(Long userId, Integer days);
}
