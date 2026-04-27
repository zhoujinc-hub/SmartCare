package com.smartcare.vo.elder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 家属端可见的老人跌倒事件VO
 */
@Data
public class ElderFallEventVO {

    /**
     * 跌倒事件ID
     */
    private Long eventId;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 家属用户ID
     */
    private Long userId;

    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 跌倒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime fallTime;

    /**
     * 检测时间 / 告警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime detectTime;

    /**
     * 位置描述
     */
    private String locationDesc;

    /**
     * 状态：1待处理 2已处理 3误报
     */
    private Integer status;

    /**
     * 截图路径
     */
    private String screenshotPath;

    /**
     * 视频路径
     */
    private String videoPath;
}