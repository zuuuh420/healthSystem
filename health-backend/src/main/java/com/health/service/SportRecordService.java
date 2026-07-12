package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.SportRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录Service接口
 */
public interface SportRecordService extends IService<SportRecord> {

    /**
     * 分页查询运动记录
     */
    IPage<SportRecord> getSportRecords(Long userId, Integer pageNum, Integer pageSize,
                                        LocalDate startDate, LocalDate endDate, Long sportTypeId);

    /**
     * 根据ID查询运动记录
     */
    SportRecord getSportRecordById(Long id, Long userId);

    /**
     * 新增运动记录
     */
    boolean addSportRecord(SportRecord sportRecord);

    /**
     * 修改运动记录
     */
    boolean updateSportRecord(SportRecord sportRecord, Long userId);

    /**
     * 删除运动记录
     */
    boolean deleteSportRecord(Long id, Long userId);

    /**
     * 获取今日运动记录
     */
    List<SportRecord> getTodayRecords(Long userId);
}
