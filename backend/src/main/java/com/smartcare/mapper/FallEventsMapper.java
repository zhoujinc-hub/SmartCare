package com.smartcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcare.entity.FallEvents;
import com.smartcare.vo.elder.ElderFallEventVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 跌倒事件Mapper
 */
@Mapper
public interface FallEventsMapper extends BaseMapper<FallEvents> {

    /**
     * 查询当前家属绑定老人对应的跌倒事件总数
     */
    @Select("""
        SELECT COUNT(1)
        FROM fall_events fe
        INNER JOIN relations r ON fe.elder_id = r.elder_id
        WHERE r.user_id = #{userId}
          AND fe.elder_id = #{elderId}
    """)
    Long countFamilyFallEvents(@Param("userId") Long userId,
                               @Param("elderId") Long elderId);

    /**
     * 分页查询当前家属绑定老人对应的跌倒事件
     */
    @Select("""
        SELECT
            fe.event_id AS eventId,
            fe.elder_id AS elderId,
            r.user_id AS userId,
            fe.elder_name AS elderName,
            fe.fall_time AS fallTime,
            fe.detect_time AS detectTime,
            NULL AS locationDesc,
            fe.status AS status,
            fe.screenshot_path AS screenshotPath,
            fe.video_path AS videoPath
        FROM fall_events fe
        INNER JOIN relations r ON fe.elder_id = r.elder_id
        WHERE r.user_id = #{userId}
          AND fe.elder_id = #{elderId}
        ORDER BY fe.detect_time DESC
        LIMIT #{offset}, #{pageSize}
    """)
    List<ElderFallEventVO> selectFamilyFallEventPage(@Param("userId") Long userId,
                                                     @Param("elderId") Long elderId,
                                                     @Param("offset") Integer offset,
                                                     @Param("pageSize") Integer pageSize);
}