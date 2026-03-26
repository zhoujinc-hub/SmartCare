package com.smartcare.controller;

import com.smartcare.common.Result;
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
        return Result.ok(usersService.login(dto));
    }
    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDto dto) {
        usersService.resetPassword(dto);
        return Result.ok();
    }

}