package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.ResetPasswordDto;
import com.smartcare.entity.Users;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.UsersService;
import com.smartcare.vo.user.LoginVo;
import com.smartcare.vo.user.UserSimpleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersMapper usersMapper;

    @Override
    public LoginVo login(LoginDto dto) {
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getUsername, dto.getUsername());
        wrapper.eq(Users::getPassword, dto.getPassword());
        wrapper.eq(Users::getUserType, dto.getUserType());

        // 2. 查询用户
        Users user = usersMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new RuntimeException("该用户已被禁用");
        }
        //此处为封装成前端需要的信息返回给前端
        LoginVo vo = new LoginVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
    @Override
    public void resetPassword(ResetPasswordDto dto) {
        Users user = usersMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(dto.getNewPassword());
        usersMapper.updateById(user);
    }

}