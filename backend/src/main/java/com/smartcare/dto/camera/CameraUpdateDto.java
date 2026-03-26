package com.smartcare.dto.camera;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CameraUpdateDto {
    private Long cameraId;
    private Byte cameraType;
    private String cameraName;
    private String deviceSerial;
    private String streamUrl;
    private String locationDesc;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Byte status;
}