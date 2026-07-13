package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthBloodSugar;
import java.time.LocalDate;
import java.util.List;

public interface HealthBloodSugarService extends IService<HealthBloodSugar> {

    IPage<HealthBloodSugar> getList(Long userId, Integer pageNum, Integer pageSize,
                                   LocalDate startDate, LocalDate endDate);

    List<HealthBloodSugar> getTrend(Long userId, int limit);

    HealthBloodSugar getById(Long id, Long userId);

    boolean add(HealthBloodSugar record);

    boolean update(HealthBloodSugar record, Long userId);

    boolean delete(Long id, Long userId);
}
