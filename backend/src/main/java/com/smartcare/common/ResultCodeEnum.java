package com.smartcare.common;

public enum ResultCodeEnum {

    // 通用
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数错误"),
    DATA_NOT_EXIST(203, "数据不存在"),
    DATA_ALREADY_EXIST(204, "数据已存在"),

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
    ELDER_NOT_EXIST(400, "老人不存在"),
    ELDER_NAME_EMPTY(401, "老人姓名不能为空"),
    ELDER_SAVE_ERROR(402, "老人新增失败"),
    ELDER_UPDATE_ERROR(403, "老人修改失败"),
    ELDER_DELETE_ERROR(404, "老人删除失败"),

    // 关系模块
    RELATION_NOT_EXIST(500, "关联关系不存在"),
    RELATION_SAVE_ERROR(501, "关联关系保存失败"),
    RELATION_DELETE_ERROR(502, "关联关系删除失败"),

    // 摄像头模块
    CAMERA_NOT_EXIST(600, "摄像头不存在"),
    CAMERA_NAME_EMPTY(601, "摄像头名称不能为空"),
    DEVICE_SERIAL_EMPTY(602, "设备序列号不能为空"),
    DEVICE_SERIAL_EXIST(603, "设备序列号已存在"),
    CAMERA_SAVE_ERROR(604, "摄像头新增失败"),
    CAMERA_UPDATE_ERROR(605, "摄像头修改失败"),
    CAMERA_DELETE_ERROR(606, "摄像头删除失败"),
    CAMERA_REFRESH_ERROR(607, "摄像头状态刷新失败"),

    // 跌倒告警模块
    FALL_EVENT_NOT_EXIST(700, "跌倒事件不存在"),
    FALL_EVENT_ALREADY_HANDLED(701, "该告警已处理"),
    FALL_EVENT_HANDLE_ERROR(702, "告警处理失败"),
    FALL_EVENT_NOTE_ERROR(703, "处理备注保存失败");

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