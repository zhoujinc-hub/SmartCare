package com.smartcare.vo.fall;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FallEventVo {
    private Long eventId;
    private Long cameraId;
    private Long elderId;
    private String elderName;
    private Byte isRegistered;
    private LocalDateTime fallTime;
    private LocalDateTime detectTime;
    private String videoPath;
    private String screenshotPath;
    private BigDecimal confidence;
    private Byte status;
    private Long processedBy;
    private LocalDateTime processedAt;
    private String processNotes;
}