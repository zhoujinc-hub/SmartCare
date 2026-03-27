package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送状态枚举
 */
@Getter
@AllArgsConstructor
public enum SendStatusEnum implements BaseEnum {
    PENDING(0, "待发送"),
    SUCCESS(1, "发送成功"),
    FAILED(2, "发送失败");

    private final Integer code;
    private final String desc;
}