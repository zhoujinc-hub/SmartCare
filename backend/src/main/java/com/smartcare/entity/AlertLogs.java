package com.smartcare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "报警日志表")
@TableName("alert_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class AlertLogs extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报警日志ID")
    @TableId(value = "alert_id", type = IdType.AUTO)
    private Long alertId;

    @Schema(description = "跌倒事件ID")
    @TableField("event_id")
    private Long eventId;

    @Schema(description = "接收人ID")
    @TableField("recipient_id")
    private Long recipientId;

    @Schema(description = "接收人类型 1:家属 2:管理员")
    @TableField("recipient_type")
    private Byte recipientType;

    @Schema(description = "接收手机号")
    @TableField("recipient_phone")
    private String recipientPhone;

    @Schema(description = "发送方式 1:短信 2:APP推送 3:电话")
    @TableField("send_method")
    private Byte sendMethod;

    @Schema(description = "发送状态 0:失败 1:成功 2:发送中")
    @TableField("send_status")
    private Byte sendStatus;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "发送时间")
    @TableField("sent_at")
    private LocalDateTime sentAt;
}