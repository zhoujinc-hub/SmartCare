package com.smartcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcare.entity.Relations;
import com.smartcare.vo.elder.ElderRelativeVo;
import com.smartcare.vo.relation.RelationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 老人与家属关系Mapper
 */
@Mapper
public interface RelationsMapper extends BaseMapper<Relations> {

    Page<RelationVo> selectRelationPage(Page<RelationVo> page, @Param("elderId") Long elderId);

    List<ElderRelativeVo> selectRelativeListByElderId(@Param("elderId") Long elderId);

    /**
     * 家属端：判断当前登录家属是否绑定了该老人
     */
    @Select("""
        SELECT COUNT(1)
        FROM relations
        WHERE user_id = #{userId}
          AND elder_id = #{elderId}
    """)
    int countFamilyElderRelation(@Param("userId") Long userId,
                                 @Param("elderId") Long elderId);
}