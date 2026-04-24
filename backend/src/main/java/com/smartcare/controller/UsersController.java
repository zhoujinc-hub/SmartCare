package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.RegisterDto;
import com.smartcare.dto.user.ResetPasswordRequestDto;
import com.smartcare.service.UsersService;
import com.smartcare.vo.user.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto dto) {
        return Result.ok(usersService.login(dto));
    }

    // 发送验证码
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody SendCodeRequest request) {
        usersService.sendCode(request.getPhone());
        return Result.ok();
    }

    // 注册
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDto dto) {
        usersService.register(dto);
        return Result.ok();
    }

    // 重置密码
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        usersService.resetPasswordByPhone(dto);
        return Result.ok();
    }

    // 内部类：接收手机号
    @lombok.Data
    public static class SendCodeRequest {
        private String phone;
    }
}