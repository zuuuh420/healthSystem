package com.health.service;

import com.health.vo.DashboardOverviewVO;

public interface DashboardService {
    DashboardOverviewVO getOverview(Long userId);
}
