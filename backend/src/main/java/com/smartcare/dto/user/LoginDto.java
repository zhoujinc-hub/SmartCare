package com.smartcare.dto.user;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private Byte userType;
}