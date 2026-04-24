package com.smartcare.service;

import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.RegisterDto;
import com.smartcare.dto.user.ResetPasswordRequestDto;
import com.smartcare.vo.user.LoginVo;

public interface UsersService {

    LoginVo login(LoginDto dto);


    void sendCode(String phone);

    void register(RegisterDto dto);

    void resetPasswordByPhone(ResetPasswordRequestDto dto);
}