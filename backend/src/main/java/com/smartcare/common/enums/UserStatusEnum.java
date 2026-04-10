package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements BaseEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用"),
    LOCKED(2, "锁定");

    private final Integer code;
    private final String desc;
}