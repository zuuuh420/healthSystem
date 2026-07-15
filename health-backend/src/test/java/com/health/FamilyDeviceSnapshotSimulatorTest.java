package com.health;

import com.health.entity.FamilyDeviceSnapshot;
import com.health.service.FamilyDeviceSnapshotSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FamilyDeviceSnapshotSimulatorTest {

    @Test
    void wearingSnapshotChangesWithinSafeBounds() {
        FamilyDeviceSnapshot snapshot = wearingSnapshot();

        FamilyDeviceSnapshotSimulator.advance(snapshot, 4, true, -20);

        assertThat(snapshot.getHeartRate()).isEqualTo(78);
        assertThat(snapshot.getOxygen()).isEqualTo(98);
        assertThat(snapshot.getSteps()).isEqualTo(6842);
    }

    @Test
    void notWearingSnapshotNeverExposesMetrics() {
        FamilyDeviceSnapshot snapshot = wearingSnapshot();
        snapshot.setWearing(false);

        FamilyDeviceSnapshotSimulator.advance(snapshot, 1, false, 10);

        assertThat(snapshot.getHeartRate()).isNull();
        assertThat(snapshot.getOxygen()).isNull();
        assertThat(snapshot.getTemperature()).isNull();
        assertThat(snapshot.getSleepMinutes()).isNull();
        assertThat(snapshot.getSteps()).isNull();
    }

    private FamilyDeviceSnapshot wearingSnapshot() {
        FamilyDeviceSnapshot snapshot = new FamilyDeviceSnapshot();
        snapshot.setDeviceOnline(true);
        snapshot.setWearing(true);
        snapshot.setHeartRate(77);
        snapshot.setOxygen(99);
        snapshot.setTemperature(36.8);
        snapshot.setSleepMinutes(438);
        snapshot.setSteps(6842);
        return snapshot;
    }
}
