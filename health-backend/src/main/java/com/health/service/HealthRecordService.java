package com.health.service;

import com.health.entity.HealthRecord;
import java.time.LocalDate;
import java.util.List;

public interface HealthRecordService {
    boolean addHealthRecord(HealthRecord record);
    boolean updateHealthRecord(HealthRecord record, Long userId);
    boolean deleteHealthRecord(Long id, Long userId);
    List<HealthRecord> listByUser(Long userId);
    List<HealthRecord> listByDateRange(Long userId, LocalDate start, LocalDate end);
}
