package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthWeight;
import java.time.LocalDate;
import java.util.List;

public interface HealthWeightService extends IService<HealthWeight> {

    IPage<HealthWeight> getWeightList(Long userId, Integer pageNum, Integer pageSize,
                                     LocalDate startDate, LocalDate endDate);

    List<HealthWeight> getWeightTrend(Long userId, int limit);

    HealthWeight getWeightById(Long id, Long userId);

    boolean addWeight(HealthWeight record);

    boolean updateWeight(HealthWeight record, Long userId);

    boolean deleteWeight(Long id, Long userId);
}
