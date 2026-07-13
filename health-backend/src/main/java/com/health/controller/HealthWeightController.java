package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.HealthWeightDTO;
import com.health.entity.HealthWeight;
import com.health.service.HealthWeightService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/health-weight")
public class HealthWeightController {

    @Autowired
    private HealthWeightService healthWeightService;

    @GetMapping
    public Result<IPage<HealthWeight>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(healthWeightService.getWeightList(userId, pageNum, pageSize, startDate, endDate));
    }

    @GetMapping("/trend")
    public Result<List<HealthWeight>> trend(@RequestParam(defaultValue = "30") int limit) {
        return Result.success(healthWeightService.getWeightTrend(SecurityUtil.getCurrentUserId(), limit));
    }

    @GetMapping("/{id}")
    public Result<HealthWeight> detail(@PathVariable Long id) {
        HealthWeight r = healthWeightService.getWeightById(id, SecurityUtil.getCurrentUserId());
        return r != null ? Result.success(r) : Result.error("记录不存在");
    }

    @PostMapping
    public Result<HealthWeight> add(@Valid @RequestBody HealthWeightDTO dto) {
        HealthWeight r = new HealthWeight();
        BeanUtils.copyProperties(dto, r);
        r.setUserId(SecurityUtil.getCurrentUserId());
        return healthWeightService.addWeight(r) ? Result.success("新增成功", r) : Result.error("新增失败");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HealthWeightDTO dto) {
        HealthWeight r = new HealthWeight();
        BeanUtils.copyProperties(dto, r);
        r.setId(id);
        return healthWeightService.updateWeight(r, SecurityUtil.getCurrentUserId())
                ? Result.success("修改成功", null) : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return healthWeightService.deleteWeight(id, SecurityUtil.getCurrentUserId())
                ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}
