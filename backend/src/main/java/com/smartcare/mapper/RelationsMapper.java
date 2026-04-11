package com.smartcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.entity.Relations;
import com.smartcare.vo.relation.RelationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RelationsMapper extends BaseMapper<Relations> {
    Page<RelationVo> selectRelationPage(Page<RelationVo> page, @Param("elderId") Long elderId);
}