package com.smartcare.common;

public enum ResultCodeEnum {

    // 通用
    SUCCESS(200, "成功"),
    FAIL(500, "系统异常"),
    PARAM_ERROR(400, "参数错误"),
    DATA_NOT_EXIST(404, "数据不存在"),
    DATA_ALREADY_EXIST(409, "数据已存在"),

    // 用户模块
    LOGIN_ERROR(300, "用户名或密码错误"),
    ACCOUNT_DISABLED(301, "账号已被禁用"),
    USER_NOT_EXIST(302, "用户不存在"),
    PHONE_NOT_EXIST(303, "手机号不存在"),
    CODE_ERROR(304, "验证码错误"),
    CODE_EXPIRED(305, "验证码已过期"),
    RESET_PASSWORD_ERROR(306, "密码重置失败"),
    RELATIVE_NOT_EXIST(307, "家属不存在"),

    // 老人模块
    ELDER_NOT_EXIST(4001, "老人不存在"),
    ELDER_NAME_EMPTY(4002, "老人姓名不能为空"),
    ELDER_SAVE_ERROR(4003, "老人新增失败"),
    ELDER_UPDATE_ERROR(4004, "老人修改失败"),
    ELDER_DELETE_ERROR(4005, "老人删除失败"),

    // 关系模块
    RELATION_NOT_EXIST(5001, "关联关系不存在"),
    RELATION_SAVE_ERROR(5002, "关联关系保存失败"),
    RELATION_DELETE_ERROR(5003, "关联关系删除失败"),

    // 摄像头模块
    CAMERA_NOT_EXIST(6001, "摄像头不存在"),
    CAMERA_NAME_EMPTY(6002, "摄像头名称不能为空"),
    DEVICE_SERIAL_EMPTY(6003, "设备序列号不能为空"),
    DEVICE_SERIAL_EXIST(6004, "设备序列号已存在"),
    CAMERA_SAVE_ERROR(6005, "摄像头新增失败"),
    CAMERA_UPDATE_ERROR(6006, "摄像头修改失败"),
    CAMERA_DELETE_ERROR(6007, "摄像头删除失败"),
    CAMERA_REFRESH_ERROR(6008, "摄像头状态刷新失败"),

    // 跌倒告警模块
    FALL_EVENT_NOT_EXIST(7001, "跌倒事件不存在"),
    FALL_EVENT_ALREADY_HANDLED(7002, "该告警已处理"),
    FALL_EVENT_HANDLE_ERROR(7003, "告警处理失败"),
    FALL_EVENT_NOTE_ERROR(7004, "处理备注保存失败");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}