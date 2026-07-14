package com.health.controller;

import com.health.common.Result;
import com.health.service.FamilyHealthSnapshotService;
import com.health.vo.FamilyHealthSnapshotVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/family")
public class FamilyHealthSnapshotController {

    private final FamilyHealthSnapshotService snapshotService;

    public FamilyHealthSnapshotController(FamilyHealthSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @GetMapping("/health-snapshots")
    public Result<List<FamilyHealthSnapshotVO>> list() {
        try {
            return Result.success(snapshotService.listAuthorizedSnapshots());
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }
}
