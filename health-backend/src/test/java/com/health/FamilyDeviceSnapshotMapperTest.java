package com.health;

import com.health.entity.FamilyDeviceSnapshot;
import com.health.mapper.FamilyDeviceSnapshotMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FamilyDeviceSnapshotMapperTest {

    @Autowired
    private FamilyDeviceSnapshotMapper mapper;

    @Test
    void findByMemberIdsReturnsRequestedSnapshots() {
        List<FamilyDeviceSnapshot> snapshots = mapper.findByMemberIds(Arrays.asList(2L, 3L));

        assertThat(snapshots).extracting(FamilyDeviceSnapshot::getMemberUserId)
                .containsExactlyInAnyOrder(2L, 3L);
    }
}
