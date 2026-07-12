package com.health.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡相关VO
 */
@Data
public class CheckinVO {

    /**
     * 今日是否已打卡
     */
    private Boolean checkedIn;

    /**
     * 打卡时间
     */
    private LocalDateTime checkinTime;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最大连续打卡天数
     */
    private Integer maxStreak;

    /**
     * 总打卡次数
     */
    private Integer totalCheckins;

    /**
     * 某月打卡日历
     */
    private String month;

    /**
     * 打卡日期列表
     */
    private List<Integer> checkinDays;

    /**
     * 当月打卡总天数
     */
    private Integer totalDays;
}
