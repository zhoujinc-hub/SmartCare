package com.smartcare.service;

import com.smartcare.dto.relation.RelationQueryDto;
import com.smartcare.vo.Page.PageVo;
import com.smartcare.vo.relation.RelationVo;

public interface RelationsService {

    PageVo<RelationVo> list(RelationQueryDto dto);
}