package com.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.Result;
import com.health.common.SecurityUtil;
import com.health.dto.HealthBloodPressureDTO;
import com.health.entity.HealthBloodPressure;
import com.health.service.HealthBloodPressureService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/health-blood-pressure")
public class HealthBloodPressureController {

    @Autowired
    private HealthBloodPressureService service;

    @GetMapping
    public Result<IPage<HealthBloodPressure>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return Result.success(service.getList(SecurityUtil.getCurrentUserId(), pageNum, pageSize, startDate, endDate));
    }

    @GetMapping("/trend")
    public Result<List<HealthBloodPressure>> trend(@RequestParam(defaultValue = "30") int limit) {
        return Result.success(service.getTrend(SecurityUtil.getCurrentUserId(), limit));
    }

    @GetMapping("/{id}")
    public Result<HealthBloodPressure> detail(@PathVariable Long id) {
        HealthBloodPressure r = service.getById(id, SecurityUtil.getCurrentUserId());
        return r != null ? Result.success(r) : Result.error("记录不存在");
    }

    @PostMapping
    public Result<HealthBloodPressure> add(@Valid @RequestBody HealthBloodPressureDTO dto) {
        HealthBloodPressure r = new HealthBloodPressure();
        BeanUtils.copyProperties(dto, r);
        r.setUserId(SecurityUtil.getCurrentUserId());
        return service.add(r) ? Result.success("新增成功", r) : Result.error("新增失败");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HealthBloodPressureDTO dto) {
        HealthBloodPressure r = new HealthBloodPressure();
        BeanUtils.copyProperties(dto, r);
        r.setId(id);
        return service.update(r, SecurityUtil.getCurrentUserId())
                ? Result.success("修改成功", null) : Result.error("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id, SecurityUtil.getCurrentUserId())
                ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}
