package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthBloodPressure;
import java.time.LocalDate;
import java.util.List;

public interface HealthBloodPressureService extends IService<HealthBloodPressure> {

    IPage<HealthBloodPressure> getList(Long userId, Integer pageNum, Integer pageSize,
                                      LocalDate startDate, LocalDate endDate);

    List<HealthBloodPressure> getTrend(Long userId, int limit);

    HealthBloodPressure getById(Long id, Long userId);

    boolean add(HealthBloodPressure record);

    boolean update(HealthBloodPressure record, Long userId);

    boolean delete(Long id, Long userId);
}
