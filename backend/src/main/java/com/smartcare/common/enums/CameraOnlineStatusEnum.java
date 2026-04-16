package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 摄像头在线状态枚举
 */
@Getter
@AllArgsConstructor
public enum CameraOnlineStatusEnum implements BaseEnum {
    ONLINE(0, "在线"),
    OFFLINE(1, "离线"),
    ABNORMAL(2, "异常");

    private final Integer code;
    private final String desc;
}