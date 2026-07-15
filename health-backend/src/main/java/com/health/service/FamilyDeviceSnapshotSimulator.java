package com.health.service;

import com.health.entity.FamilyDeviceSnapshot;

public final class FamilyDeviceSnapshotSimulator {

    private FamilyDeviceSnapshotSimulator() {
    }

    public static void advance(FamilyDeviceSnapshot snapshot, int requestedHeartRateDelta,
                               boolean dropOxygen, int requestedStepDelta) {
        if (!Boolean.TRUE.equals(snapshot.getDeviceOnline()) || !Boolean.TRUE.equals(snapshot.getWearing())) {
            snapshot.setHeartRate(null);
            snapshot.setOxygen(null);
            snapshot.setTemperature(null);
            snapshot.setSleepMinutes(null);
            snapshot.setSteps(null);
            return;
        }

        int heartRate = snapshot.getHeartRate() == null ? 77 : snapshot.getHeartRate();
        int delta = Math.max(-1, Math.min(1, requestedHeartRateDelta));
        snapshot.setHeartRate(Math.max(74, Math.min(80, heartRate + delta)));

        int oxygen = snapshot.getOxygen() == null ? 99 : snapshot.getOxygen();
        snapshot.setOxygen(dropOxygen ? 98 : Math.max(98, Math.min(99, oxygen)));
        double temperature = snapshot.getTemperature() == null ? 36.8 : snapshot.getTemperature();
        snapshot.setTemperature(Math.max(36.7, Math.min(36.9, temperature)));
        if (snapshot.getSleepMinutes() == null) snapshot.setSleepMinutes(438);

        int steps = snapshot.getSteps() == null ? 0 : snapshot.getSteps();
        snapshot.setSteps(steps + Math.max(0, requestedStepDelta));
    }
}
