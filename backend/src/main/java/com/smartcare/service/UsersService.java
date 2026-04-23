package com.smartcare.service;

import com.smartcare.dto.user.RegisterDto;
import com.smartcare.dto.user.ResetPasswordRequestDto;

public interface UsersService {

    void sendCode(String phone);

    void register(RegisterDto dto);

    void resetPasswordByPhone(ResetPasswordRequestDto dto);
}