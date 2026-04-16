package com.smartcare.common.enums;

/**
 * 基础枚举接口
 * 作用：统一所有枚举的行为，方便全局工具类处理
 */
public interface BaseEnum {

    Integer getCode();

    String getDesc();
}