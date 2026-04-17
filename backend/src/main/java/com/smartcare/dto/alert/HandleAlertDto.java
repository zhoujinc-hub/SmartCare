package com.smartcare.dto.alert;

import lombok.Data;

@Data
public class HandleAlertDto {

    private Long alertId;

    private Byte sendStatus; // 1成功 0失败
}