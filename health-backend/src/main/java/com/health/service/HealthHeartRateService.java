package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthHeartRate;
import java.time.LocalDate;
import java.util.List;

public interface HealthHeartRateService extends IService<HealthHeartRate> {

    IPage<HealthHeartRate> getList(Long userId, Integer pageNum, Integer pageSize,
                                  LocalDate startDate, LocalDate endDate);

    List<HealthHeartRate> getTrend(Long userId, int limit);

    HealthHeartRate getById(Long id, Long userId);

    boolean add(HealthHeartRate record);

    boolean update(HealthHeartRate record, Long userId);

    boolean delete(Long id, Long userId);
}
