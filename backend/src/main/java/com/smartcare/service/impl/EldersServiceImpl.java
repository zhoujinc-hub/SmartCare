package com.smartcare.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderSaveDto;
import com.smartcare.dto.elder.ElderUpdateDto;
import com.smartcare.entity.Elders;
import com.smartcare.entity.Relations;
import com.smartcare.mapper.EldersMapper;
import com.smartcare.mapper.RelationsMapper;
import com.smartcare.service.EldersService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.elder.ElderDetailVo;
import com.smartcare.vo.elder.ElderListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class EldersServiceImpl implements EldersService {
    private final EldersMapper eldersMapper;
    private final RelationsMapper relationsMapper;

    @Override
    public PageVo<ElderListVo> list(ElderQueryDto dto) {
        LambdaQueryWrapper<Elders> wrapper = new LambdaQueryWrapper<>();
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            wrapper.like(Elders::getName, dto.getName());
        }
        wrapper.orderByDesc(Elders::getElderId);

        Page<Elders> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Elders> elderPage = eldersMapper.selectPage(page, wrapper);

        List<ElderListVo> voList = new ArrayList<>();
        for (Elders elder : elderPage.getRecords()) {
            ElderListVo vo = new ElderListVo();
            BeanUtils.copyProperties(elder, vo);
            voList.add(vo);
        }

        PageVo<ElderListVo> result = new PageVo<>();
        result.setTotal(elderPage.getTotal());
        result.setPages(elderPage.getPages());
        result.setPageNum(elderPage.getCurrent());
        result.setPageSize(elderPage.getSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    public void add(ElderSaveDto dto) {

    }

    @Override
    public void update(ElderUpdateDto dto) {

    }

    @Override
    public void delete(Long elderId) {

    }

    @Override
    public ElderDetailVo detail(Long elderId) {
        return null;
    }

}