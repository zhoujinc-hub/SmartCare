package com.smartcare.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.dto.elder.ElderQueryDto;
import com.smartcare.dto.elder.ElderRelativeDto;
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
    @Transactional(rollbackFor = Exception.class)
    public void add(ElderSaveDto dto) {
        Elders elder = new Elders();
        BeanUtils.copyProperties(dto, elder);
        eldersMapper.insert(elder);

        if (dto.getRelatives() != null && !dto.getRelatives().isEmpty()) {
            for (ElderRelativeDto relativeDto : dto.getRelatives()) {
                Relations relation = new Relations();
                relation.setElderId(elder.getElderId());
                relation.setUserId(relativeDto.getUserId());
                relation.setRelationship(relativeDto.getRelationship());
                relationsMapper.insert(relation);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ElderUpdateDto dto) {
        Elders elder = eldersMapper.selectById(dto.getElderId());

        if (elder == null) {
            throw new RuntimeException("老人不存在");
        }

        BeanUtils.copyProperties(dto, elder);
        eldersMapper.updateById(elder);
        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, dto.getElderId())
        );

        if (dto.getRelatives() != null && !dto.getRelatives().isEmpty()) {
            for (ElderRelativeDto relativeDto : dto.getRelatives()) {
                Relations relation = new Relations();
                relation.setElderId(dto.getElderId());
                relation.setUserId(relativeDto.getUserId());
                relation.setRelationship(relativeDto.getRelationship());
                relationsMapper.insert(relation);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long elderId) {
        Elders elder = eldersMapper.selectById(elderId);
        if (elder == null) {
            throw new RuntimeException("老人不存在");
        }
        relationsMapper.delete(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, elderId)
        );
        eldersMapper.deleteById(elderId);
    }

    @Override
    public ElderDetailVo detail(Long elderId) {
        Elders elder = eldersMapper.selectById(elderId);

        if (elder == null) {
            throw new RuntimeException("老人不存在");
        }

        ElderDetailVo detailVo = new ElderDetailVo();
        BeanUtils.copyProperties(elder, detailVo);

        List<Relations> relations = relationsMapper.selectList(
                new LambdaQueryWrapper<Relations>()
                        .eq(Relations::getElderId, elderId)
        );

        List<ElderRelativeDto> relatives = new ArrayList<>();
        for (Relations relation : relations) {
            ElderRelativeDto dto = new ElderRelativeDto();
            dto.setUserId(relation.getUserId());
            dto.setRelationship(relation.getRelationship());
            relatives.add(dto);
        }

        detailVo.setRelatives(relatives);
        return detailVo;
    }
}