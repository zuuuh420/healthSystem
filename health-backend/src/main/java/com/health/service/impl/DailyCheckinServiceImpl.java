package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.DailyCheckin;
import com.health.mapper.DailyCheckinMapper;
import com.health.service.DailyCheckinService;
import com.health.vo.CheckinVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 每日打卡Service实现类
 */
@Service
public class DailyCheckinServiceImpl extends ServiceImpl<DailyCheckinMapper, DailyCheckin> implements DailyCheckinService {

    @Autowired
    private DailyCheckinMapper dailyCheckinMapper;

    @Override
    public boolean checkin(Long userId, Long sportRecordId, String checkinType) {
        LocalDate today = LocalDate.now();

        // 检查今日是否已打卡
        LambdaQueryWrapper<DailyCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyCheckin::getUserId, userId)
               .eq(DailyCheckin::getCheckinDate, today)
               .eq(DailyCheckin::getCheckinType, checkinType);

        if (count(wrapper) > 0) {
            return false; // 今日已打卡
        }

        // 创建打卡记录
        DailyCheckin checkin = new DailyCheckin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(today);
        checkin.setSportRecordId(sportRecordId);
        checkin.setCheckinType(checkinType);
        checkin.setCreateTime(LocalDateTime.now());

        return save(checkin);
    }

    @Override
    public CheckinVO getTodayCheckinStatus(Long userId, String checkinType) {
        CheckinVO vo = new CheckinVO();
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<DailyCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyCheckin::getUserId, userId)
               .eq(DailyCheckin::getCheckinDate, today)
               .eq(DailyCheckin::getCheckinType, checkinType);

        DailyCheckin checkin = getOne(wrapper);
        vo.setCheckedIn(checkin != null);
        if (checkin != null) {
            vo.setCheckinTime(checkin.getCreateTime());
        }

        return vo;
    }

    @Override
    public CheckinVO getCheckinStreak(Long userId, String checkinType) {
        CheckinVO vo = new CheckinVO();

        // 查询所有打卡日期（按日期降序）
        LambdaQueryWrapper<DailyCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyCheckin::getUserId, userId)
               .eq(DailyCheckin::getCheckinType, checkinType)
               .orderByDesc(DailyCheckin::getCheckinDate);

        List<DailyCheckin> checkins = list(wrapper);
        vo.setTotalCheckins(checkins.size());

        if (checkins.isEmpty()) {
            vo.setCurrentStreak(0);
            vo.setMaxStreak(0);
            return vo;
        }

        // 计算当前连续天数
        int currentStreak = 0;
        LocalDate expectedDate = LocalDate.now();

        for (DailyCheckin checkin : checkins) {
            if (checkin.getCheckinDate().equals(expectedDate)) {
                currentStreak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (checkin.getCheckinDate().isBefore(expectedDate)) {
                break;
            }
        }

        vo.setCurrentStreak(currentStreak);

        // 计算最大连续天数
        int maxStreak = 0;
        int tempStreak = 1;
        LocalDate previousDate = null;

        for (int i = checkins.size() - 1; i >= 0; i--) {
            LocalDate currentDate = checkins.get(i).getCheckinDate();
            if (previousDate != null && currentDate.equals(previousDate.plusDays(1))) {
                tempStreak++;
            } else {
                tempStreak = 1;
            }
            maxStreak = Math.max(maxStreak, tempStreak);
            previousDate = currentDate;
        }

        vo.setMaxStreak(maxStreak);

        return vo;
    }

    @Override
    public CheckinVO getCheckinCalendar(Long userId, String checkinType, int year, int month) {
        CheckinVO vo = new CheckinVO();
        vo.setMonth(year + "-" + String.format("%02d", month));

        List<LocalDate> dates = dailyCheckinMapper.selectCheckinDates(userId, checkinType, year, month);

        List<Integer> checkinDays = new ArrayList<>();
        for (LocalDate date : dates) {
            checkinDays.add(date.getDayOfMonth());
        }

        vo.setCheckinDays(checkinDays);
        vo.setTotalDays(checkinDays.size());

        return vo;
    }
}
