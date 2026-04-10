package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements BaseEnum {
    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知");

    private final Integer code;
    private final String desc;
}