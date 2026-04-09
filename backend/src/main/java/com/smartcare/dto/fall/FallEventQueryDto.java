package com.smartcare.dto.fall;

import lombok.Data;

@Data
public class FallEventQueryDto {
    private String elderName;
    private Byte status;
    private Long cameraId;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}