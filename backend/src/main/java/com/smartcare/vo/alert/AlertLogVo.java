package com.smartcare.vo.alert;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertLogVo {
    private Long alertId;
    private Long eventId;
    private Long recipientId;
    private Byte recipientType;
    private String recipientPhone;
    private Byte sendMethod;
    private Byte sendStatus;
    private String errorMsg;
    private LocalDateTime sentAt;
}