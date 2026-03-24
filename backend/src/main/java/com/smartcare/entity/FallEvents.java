package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "跌倒事件表")
@TableName("fall_events")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class FallEvents extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "事件ID")
    @TableId(value = "event_id", type = IdType.AUTO)
    private Long eventId;

    @Schema(description = "摄像头ID")
    @TableField("camera_id")
    private Long cameraId;

    @Schema(description = "老人ID")
    @TableField("elder_id")
    private Long elderId;

    @Schema(description = "老人姓名")
    @TableField("elder_name")
    private String elderName;

    @Schema(description = "是否注册 0:未注册 1:已注册")
    @TableField("is_registered")
    private Byte isRegistered;

    @Schema(description = "摔倒发生时间")
    @TableField("fall_time")
    private LocalDateTime fallTime;

    @Schema(description = "系统检测时间")
    @TableField("detect_time")
    private LocalDateTime detectTime;

    @Schema(description = "视频路径")
    @TableField("video_path")
    private String videoPath;

    @Schema(description = "截图路径")
    @TableField("screenshot_path")
    private String screenshotPath;

    @Schema(description = "AI置信度")
    @TableField("confidence")
    private BigDecimal confidence;

    @Schema(description = "状态 1:待处理 2:已处理 3:误报")
    @TableField("status")
    private Byte status;

    @Schema(description = "处理人ID")
    @TableField("processed_by")
    private Long processedBy;

    @Schema(description = "处理时间")
    @TableField("processed_at")
    private LocalDateTime processedAt;

    @Schema(description = "处理备注")
    @TableField("process_notes")
    private String processNotes;
}