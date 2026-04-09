package com.smartcare.dto.alert;

import lombok.Data;

@Data
public class AlertLogQueryDto {
    private Long eventId;
    private Byte sendStatus;
}