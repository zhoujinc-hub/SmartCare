package com.smartcare.vo.camera;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CameraVo {
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