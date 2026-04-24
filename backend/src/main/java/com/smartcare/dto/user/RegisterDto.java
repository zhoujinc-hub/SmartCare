package com.smartcare.dto.user;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String phone;
    // 类型从 Integer 改为 Byte
    private Byte userType;
    private String password;
    private String code;
    private String realName;
}