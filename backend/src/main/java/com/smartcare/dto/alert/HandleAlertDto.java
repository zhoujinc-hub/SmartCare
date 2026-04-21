package com.smartcare.dto.alert;

import lombok.Data;

/**
 * 告警处理入参 DTO
 */
@Data
public class HandleAlertDto {
    // 告警ID
    private Long alertId;
    // 发送状态（0/1/2 需和前端约定，对应 TS 的 0 | 1 | 2）
    private Byte sendStatus;
    // 错误信息（前端传递的异常描述）
    private String errorMsg;
}