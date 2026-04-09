package com.smartcare.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 老人状态枚举
 */
@Getter
@AllArgsConstructor
public enum ElderStatusEnum implements BaseEnum {
    AT_HOME(0, "在家"),
    OUTING(1, "外出"),
    HOSPITALIZED(2, "住院");

    private final Integer code;
    private final String desc;
}