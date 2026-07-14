package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.entity.FamilyDeviceSnapshot;
import com.health.mapper.FamilyDeviceSnapshotMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FamilyDeviceSnapshotSimulationService {

    private final FamilyDeviceSnapshotMapper mapper;

    public FamilyDeviceSnapshotSimulationService(FamilyDeviceSnapshotMapper mapper) {
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void updateOnlineSnapshots() {
        List<FamilyDeviceSnapshot> snapshots = mapper.selectList(new LambdaQueryWrapper<FamilyDeviceSnapshot>()
                .eq(FamilyDeviceSnapshot::getDeviceOnline, true)
                .eq(FamilyDeviceSnapshot::getWearing, true));
        for (FamilyDeviceSnapshot snapshot : snapshots) {
            FamilyDeviceSnapshotSimulator.advance(
                    snapshot,
                    ThreadLocalRandom.current().nextInt(-1, 2),
                    ThreadLocalRandom.current().nextInt(5) == 0,
                    ThreadLocalRandom.current().nextInt(0, 21));
            snapshot.setMeasuredAt(LocalDateTime.now());
            mapper.updateById(snapshot);
        }
    }
}
