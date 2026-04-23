import request from '@/utils/request'
import type {
    LoginForm,
    LoginResponse,
    ForgotForm,
    CodeResponse,
    ResetPwdResponse,
    RegisterForm,
    RegisterResponse
} from '@/types/user'

export const userLogin = (data: Omit<LoginForm, 'rememberMe'>) => {
    return request<LoginResponse>({
        url: '/user/login',
        method: 'POST',
        data: {
            username: data.username,
            password: data.password,
            userType: data.userType
        }
    }).then(res => res.data)
}

export const sendVerifyCode = (phone: string) => {
    return request<CodeResponse>({
        url: '/user/send-code',
        method: 'POST',
        data: { phone }
    }).then(res => res.data)
}

export const resetUserPassword = (data: Omit<ForgotForm, 'confirmPassword'>) => {
    return request<ResetPwdResponse>({
        url: '/user/resetPassword',
        method: 'POST',
        data: {
            phone: data.phone,
            code: data.code,
            new_password: data.newPassword
        }
    }).then(res => res.data)
}

export const registerUser = (data: Omit<RegisterForm, 'confirmPassword'>) => {
    return request<RegisterResponse>({
        url: '/user/register',
        method: 'POST',
        data
    }).then(res => res.data)
}