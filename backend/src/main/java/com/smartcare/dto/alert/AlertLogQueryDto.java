package com.smartcare.dto.alert;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertLogQueryDto {
    private Long eventId;
    private Byte sendStatus;
    private Byte sendMethod; // 新增：接收发送方式
    private Byte recipientType; // 新增：接收接收人类型
    private LocalDateTime startTime; // 新增：开始时间（发送时间）
    private LocalDateTime endTime; // 新增：结束时间（发送时间）
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}