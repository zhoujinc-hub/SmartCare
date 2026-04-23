package com.smartcare.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String phone;
    private String code;

    @JsonProperty("new_password")
    private String newPassword;
}