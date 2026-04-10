package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 告警级别枚举
 */
@Getter
@AllArgsConstructor
public enum AlertLevelEnum implements BaseEnum {
    INFO(0, "提示"),
    WARNING(1, "警告"),
    DANGER(2, "危险"),
    EMERGENCY(3, "紧急");

    private final Integer code;
    private final String desc;
}