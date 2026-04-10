package com.smartcare.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.dto.relation.RelationQueryDto;
import com.smartcare.mapper.RelationsMapper;
import com.smartcare.service.RelationsService;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.relation.RelationVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationsServiceImpl implements RelationsService {

    private final RelationsMapper relationsMapper;

    @Override
    public PageVo<RelationVo> list(RelationQueryDto dto) {
        Page<RelationVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<RelationVo> relationVoPage = relationsMapper.selectRelationPage(page, dto.getElderId());

        // 封装分页结果
        PageVo<RelationVo> result = new PageVo<>();
        result.setTotal(relationVoPage.getTotal());
        result.setPages(relationVoPage.getPages());
        result.setPageNum(relationVoPage.getCurrent());
        result.setPageSize(relationVoPage.getSize());
        result.setRecords(relationVoPage.getRecords());
        return result;
    }
}