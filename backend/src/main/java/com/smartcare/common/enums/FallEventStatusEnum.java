package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 摔倒事件状态枚举
 */
@Getter
@AllArgsConstructor
public enum FallEventStatusEnum implements BaseEnum {
    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    RESOLVED(2, "已解决"),
    FALSE_ALARM(3, "误报");

    private final Integer code;
    private final String desc;
}