package com.smartcare.vo.user;

import lombok.Data;

@Data
public class LoginVo {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private Byte userType;
    private Byte status;
}