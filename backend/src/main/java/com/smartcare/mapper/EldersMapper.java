package com.smartcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcare.entity.Elders;
import com.smartcare.vo.elder.ElderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 老人Mapper
 */
@Mapper
public interface EldersMapper extends BaseMapper<Elders> {

    /**
     * 查询当前家属绑定的老人列表
     */
    @Select("""
        SELECT
            e.elder_id AS elderId,
            r.user_id AS userId,
            e.name AS name,
            e.gender AS gender,
            e.age AS age,
            e.address AS address,
            e.health_notes AS healthNotes,
            e.created_at AS createdAt
        FROM elders e
        INNER JOIN relations r ON e.elder_id = r.elder_id
        WHERE r.user_id = #{userId}
        ORDER BY e.created_at DESC
    """)
    List<ElderVO> selectFamilyElderList(@Param("userId") Long userId);
}