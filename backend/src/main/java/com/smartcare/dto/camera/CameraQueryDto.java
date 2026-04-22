package com.smartcare.dto.camera;

import lombok.Data;

@Data
public class CameraQueryDto {
    /**
     * 设备名称
     */
    private String cameraName;

    /**
     * 摄像头类型
     */
    private Byte cameraType;

    /**
     * 设备状态
     */
    private Byte status;

    private Long pageNum = 1L;
    private Long pageSize = 10L;
}