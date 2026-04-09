package com.smartcare.dto.user;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private Long userId;
    private String newPassword;
}