package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.dto.CalorieStatsDTO;
import com.health.dto.DietRecordDTO;
import com.health.entity.DietRecord;
import java.time.LocalDate;
import java.util.List;

public interface DietRecordService {
    DietRecord create(Long userId, DietRecordDTO dto);
    DietRecord update(Long id, Long userId, DietRecordDTO dto);
    void delete(Long id, Long userId);
    IPage<DietRecord> page(Long userId, LocalDate date, int page, int size);
    List<DietRecord> listByRange(Long userId, LocalDate start, LocalDate end);
    CalorieStatsDTO getCalorieStats(Long userId);
}
