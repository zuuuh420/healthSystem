package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.DailyCheckin;
import com.health.vo.CheckinVO;

import java.time.LocalDate;

/**
 * 每日打卡Service接口
 */
public interface DailyCheckinService extends IService<DailyCheckin> {

    /**
     * 今日打卡
     */
    boolean checkin(Long userId, Long sportRecordId, String checkinType);

    /**
     * 查询今日是否已打卡
     */
    CheckinVO getTodayCheckinStatus(Long userId, String checkinType);

    /**
     * 查询连续打卡天数
     */
    CheckinVO getCheckinStreak(Long userId, String checkinType);

    /**
     * 查询某月打卡日历
     */
    CheckinVO getCheckinCalendar(Long userId, String checkinType, int year, int month);
}
