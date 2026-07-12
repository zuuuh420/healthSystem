package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.entity.DailyCheckin;
import com.health.entity.HealthGoal;
import com.health.mapper.DailyCheckinMapper;
import com.health.mapper.HealthGoalMapper;
import com.health.service.DashboardService;
import com.health.service.SportStatsService;
import com.health.vo.DashboardOverviewVO;
import com.health.vo.SportStatsVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final SportStatsService sportStatsService;
    private final HealthGoalMapper healthGoalMapper;
    private final DailyCheckinMapper dailyCheckinMapper;

    public DashboardServiceImpl(SportStatsService sportStatsService,
                                HealthGoalMapper healthGoalMapper,
                                DailyCheckinMapper dailyCheckinMapper) {
        this.sportStatsService = sportStatsService;
        this.healthGoalMapper = healthGoalMapper;
        this.dailyCheckinMapper = dailyCheckinMapper;
    }

    @Override
    public DashboardOverviewVO getOverview(Long userId) {
        SportStatsVO weekly = sportStatsService.getWeeklyStats(userId);
        int activeGoals = countGoals(userId, 0);
        int completedGoals = countGoals(userId, 1);
        int streak = calculateStreak(userId);
        int minutes = weekly.getTotalMinutes() == null ? 0 : weekly.getTotalMinutes();

        int activityScore = Math.min(45, (int) Math.round(minutes / 150.0 * 45));
        int habitScore = Math.min(30, streak * 5);
        int goalScore = completedGoals > 0 ? 25 : (activeGoals > 0 ? 12 : 5);
        int score = Math.min(100, activityScore + habitScore + goalScore);

        DashboardOverviewVO result = new DashboardOverviewVO();
        result.setHealthScore(score);
        result.setScoreLevel(score >= 80 ? "状态良好" : score >= 60 ? "稳步改善" : "需要关注");
        result.setSummary(buildSummary(score, minutes, streak));
        result.setWeeklyMinutes(minutes);
        result.setWeeklyCalories(weekly.getTotalCalories() == null ? 0D : weekly.getTotalCalories());
        result.setActiveGoals(activeGoals);
        result.setCompletedGoals(completedGoals);
        result.setCurrentStreak(streak);
        result.setTrend(weekly.getDailyStats());
        result.setSuggestions(buildSuggestions(minutes, streak, activeGoals));
        return result;
    }

    private int countGoals(Long userId, int status) {
        Long count = healthGoalMapper.selectCount(new LambdaQueryWrapper<HealthGoal>()
                .eq(HealthGoal::getUserId, userId).eq(HealthGoal::getStatus, status));
        return count == null ? 0 : count.intValue();
    }

    private int calculateStreak(Long userId) {
        List<DailyCheckin> checkins = dailyCheckinMapper.selectList(
                new LambdaQueryWrapper<DailyCheckin>()
                        .eq(DailyCheckin::getUserId, userId)
                        .orderByDesc(DailyCheckin::getCheckinDate));
        int streak = 0;
        LocalDate expected = LocalDate.now();
        for (DailyCheckin checkin : checkins) {
            LocalDate date = checkin.getCheckinDate();
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (date.isBefore(expected)) {
                break;
            }
        }
        return streak;
    }

    private String buildSummary(int score, int minutes, int streak) {
        if (score >= 80) return "本周保持了稳定的运动与打卡节奏，继续维持即可。";
        if (minutes == 0) return "本周还没有运动记录，从一次轻量活动开始建立节奏。";
        if (streak == 0) return "已有运动积累，连续打卡能让健康习惯更稳定。";
        return "本周正在形成规律，距离推荐的150分钟运动量还有提升空间。";
    }

    private List<String> buildSuggestions(int minutes, int streak, int activeGoals) {
        List<String> suggestions = new ArrayList<>();
        if (minutes < 150) suggestions.add("本周再完成" + (150 - minutes) + "分钟中等强度活动，可达到基础运动建议量。");
        if (streak < 3) suggestions.add("选择固定时段记录运动，先建立连续3天的轻量习惯。");
        if (activeGoals == 0) suggestions.add("新建一个可量化目标，例如每周运动150分钟。");
        if (suggestions.isEmpty()) suggestions.add("当前节奏稳定，注意训练强度与休息日之间的平衡。");
        return suggestions;
    }
}
