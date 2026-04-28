package com.smartcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcare.entity.FallEvents;
import com.smartcare.vo.AlarmMapVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AlarmMapMapper extends BaseMapper<FallEvents> {

    @Select("""
            SELECT
                fe.event_id AS eventId,
                fe.camera_id AS cameraId,
                fe.elder_id AS elderId,
                fe.elder_name AS elderName,
                c.camera_name AS cameraName,
                c.location_desc AS locationDesc,
                c.latitude AS latitude,
                c.longitude AS longitude,
                fe.fall_time AS fallTime,
                fe.detect_time AS detectTime,
                fe.screenshot_path AS screenshotPath,
                fe.video_path AS videoPath,
                fe.confidence AS confidence,
                fe.status AS status,
                al.send_status AS sendStatus,
                al.send_method AS sendMethod,
                al.sent_at AS sentAt
            FROM fall_events fe
            LEFT JOIN cameras c ON fe.camera_id = c.camera_id
            LEFT JOIN alert_logs al ON fe.event_id = al.event_id
            WHERE c.latitude IS NOT NULL
              AND c.longitude IS NOT NULL
              AND (#{status} IS NULL OR fe.status = #{status})
            ORDER BY fe.detect_time DESC
            """)
    List<AlarmMapVO> selectAlarmMapList(@Param("status") Byte status);

    @Update("""
            UPDATE fall_events
            SET status = #{status},
                processed_by = #{processedBy},
                processed_at = NOW(),
                process_notes = #{processNotes}
            WHERE event_id = #{eventId}
            """)
    int updateEventStatus(@Param("eventId") Long eventId,
                          @Param("status") Byte status,
                          @Param("processedBy") Long processedBy,
                          @Param("processNotes") String processNotes);
}
