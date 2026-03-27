package com.smartcare.controller;

import com.smartcare.common.Result;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.ResetPasswordDto;
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

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto dto) {
        return Result.ok(usersService.login(dto));
    }

}