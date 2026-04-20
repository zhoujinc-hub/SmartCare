package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.ResetPasswordDto;
import com.smartcare.entity.Users;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.UsersService;
import com.smartcare.vo.user.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

        Users user = usersMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCodeEnum.LOGIN_ERROR);
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        LoginVo vo = new LoginVo();
        BeanUtils.copyProperties(user, vo);
        vo.setToken("token_" + user.getUserId());
        return vo;
    }

    @Override
    public void resetPassword(ResetPasswordDto dto) {
        Users user = usersMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        }
        user.setPassword(dto.getNewPassword());
        usersMapper.updateById(user);
    }
}