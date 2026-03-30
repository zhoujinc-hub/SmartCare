package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.ResetPasswordDto;
import com.smartcare.entity.Users;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.UsersService;
import com.smartcare.vo.user.LoginVo;
import com.smartcare.vo.user.UserSimpleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final UsersMapper usersMapper;

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto dto) {
        try {
            LoginVo vo = usersService.login(dto);
            return Result.ok(vo);
        } catch (Exception e) {
            String msg = e.getMessage();
            if ("用户名或密码错误".equals(msg)) {
                return Result.build(null, ResultCodeEnum.LOGIN_ERROR);
            }
            if ("该用户已被禁用".equals(msg)) {
                return Result.build(null, ResultCodeEnum.ACCOUNT_DISABLED);
            }
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            usersService.resetPassword(dto);
            return Result.ok();
        } catch (Exception e) {
            String msg = e.getMessage();
            if ("用户不存在".equals(msg)) {
                return Result.build(null, ResultCodeEnum.USER_NOT_EXIST);
            }
            return Result.build(null, ResultCodeEnum.RESET_PASSWORD_ERROR);
        }
    }

}