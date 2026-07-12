package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.SportType;

/**
 * 运动类型Service接口
 */
public interface SportTypeService extends IService<SportType> {

    /**
     * 分页查询运动类型
     */
    IPage<SportType> getSportTypes(Integer pageNum, Integer pageSize, String category, String keyword);

    /**
     * 根据ID查询运动类型
     */
    SportType getSportTypeById(Long id);

    /**
     * 新增运动类型
     */
    boolean addSportType(SportType sportType);

    /**
     * 修改运动类型
     */
    boolean updateSportType(SportType sportType);

    /**
     * 删除运动类型
     */
    boolean deleteSportType(Long id);
}
