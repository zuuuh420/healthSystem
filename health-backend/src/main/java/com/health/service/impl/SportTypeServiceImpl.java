package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.SportType;
import com.health.mapper.SportTypeMapper;
import com.health.service.SportTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 运动类型Service实现类
 */
@Service
public class SportTypeServiceImpl extends ServiceImpl<SportTypeMapper, SportType> implements SportTypeService {

    @Override
    public IPage<SportType> getSportTypes(Integer pageNum, Integer pageSize, String category, String keyword) {
        LambdaQueryWrapper<SportType> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(SportType::getCategory, category);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SportType::getName, keyword);
        }

        // 按状态排序，启用的在前
        wrapper.orderByAsc(SportType::getStatus).orderByDesc(SportType::getCreateTime);

        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SportType getSportTypeById(Long id) {
        return getById(id);
    }

    @Override
    public boolean addSportType(SportType sportType) {
        sportType.setStatus(1);
        return save(sportType);
    }

    @Override
    public boolean updateSportType(SportType sportType) {
        return updateById(sportType);
    }

    @Override
    public boolean deleteSportType(Long id) {
        return removeById(id);
    }
}
