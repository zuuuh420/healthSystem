package com.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FamilyDeviceSnapshotSchemaTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void snapshotStoresWearingAndNotWearingStates() {
        jdbcTemplate.update("DELETE FROM family_device_snapshot WHERE member_user_id IN (2, 3)");
        jdbcTemplate.update("INSERT INTO family_device_snapshot " +
                "(member_user_id, device_name, device_online, wearing, heart_rate, oxygen, temperature, sleep_minutes, steps) " +
                "VALUES (2, '知衡 Band 2', 1, 1, 77, 99, 36.8, 438, 6842), " +
                "(3, '知衡 Band 2', 1, 0, NULL, NULL, NULL, NULL, NULL)");

        Integer wearingMetrics = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM family_device_snapshot WHERE member_user_id = 2 AND wearing = 1 AND heart_rate IS NOT NULL",
                Integer.class);
        Integer notWearingMetrics = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM family_device_snapshot WHERE member_user_id = 3 AND wearing = 0 AND heart_rate IS NULL AND steps IS NULL",
                Integer.class);

        assertThat(wearingMetrics).isEqualTo(1);
        assertThat(notWearingMetrics).isEqualTo(1);
        jdbcTemplate.update("DELETE FROM family_device_snapshot WHERE member_user_id IN (2, 3)");
    }
}
