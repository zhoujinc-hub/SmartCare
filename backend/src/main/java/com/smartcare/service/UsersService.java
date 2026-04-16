package com.smartcare.service;

import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.ResetPasswordDto;
import com.smartcare.vo.user.LoginVo;
import com.smartcare.vo.user.UserSimpleVo;

import java.util.List;

public interface UsersService {

    LoginVo login(LoginDto dto);

    void resetPassword(ResetPasswordDto dto);


}