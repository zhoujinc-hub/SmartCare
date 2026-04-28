package com.smartcare.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "报警地图点位VO")
public class AlarmMapVO {

    @Schema(description = "跌倒事件ID")
    private Long eventId;

    @Schema(description = "摄像头ID")
    private Long cameraId;

    @Schema(description = "老人ID")
    private Long elderId;

    @Schema(description = "老人姓名")
    private String elderName;

    @Schema(description = "摄像头名称")
    private String cameraName;

    @Schema(description = "位置描述")
    private String locationDesc;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "摔倒发生时间")
    private LocalDateTime fallTime;

    @Schema(description = "系统检测时间")
    private LocalDateTime detectTime;

    @Schema(description = "截图路径")
    private String screenshotPath;

    @Schema(description = "视频路径")
    private String videoPath;

    @Schema(description = "AI置信度")
    private BigDecimal confidence;

    @Schema(description = "事件状态 1:待处理 2:已处理 3:误报")
    private Byte status;

    @Schema(description = "报警发送状态 0:失败 1:成功 2:发送中")
    private Byte sendStatus;

    @Schema(description = "报警发送方式 1:短信 2:APP推送 3:电话")
    private Byte sendMethod;

    @Schema(description = "报警发送时间")
    private LocalDateTime sentAt;
}
