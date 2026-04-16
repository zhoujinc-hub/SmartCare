import request from '../utils/request'
import type { LoginForm, LoginResponse, ForgotForm, CodeResponse, ResetPwdResponse } from '../types/user'

/**
 * 用户登录接口
 * @param data 登录表单数据
 */
export const userLogin = (data: Omit<LoginForm, 'rememberMe'>) => {
  return request<LoginResponse>({
    url: '/api/user/login',
    method: 'POST',
    data: {
      username: data.username,
      password: data.password,
      user_type: data.userType
    }
  })
}

/**
 * 发送验证码接口
 * @param phone 手机号
 */
export const sendVerifyCode = (phone: string) => {
  return request<CodeResponse>({
    url: '/api/user/send-code',
    method: 'POST',
    data: { phone }
  })
}

/**
 * 重置密码接口
 * @param data 重置密码表单数据
 */
export const resetUserPassword = (data: Omit<ForgotForm, 'confirmPassword'>) => {
  return request<ResetPwdResponse>({
    url: '/api/user/reset-password',
    method: 'POST',
    data: {
      phone: data.phone,
      code: data.code,
      new_password: data.newPassword
    }
  })
}

import type { UserItem, BaseResponse } from '../types/user';

/**
 * 获取家属列表（关联users表，user_type=2）
 * @returns 家属列表
 */
export const getRelativeList = async (): Promise<BaseResponse & { data: UserItem[] }> => {
  return request({
    url: '/api/user/relative/list',
    method: 'GET'
  });
};