package com.smartcare.dto.camera;

import lombok.Data;

@Data
public class CameraQueryDto {
    private String cameraName;
    private Byte status;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}