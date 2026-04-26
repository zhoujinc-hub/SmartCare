import request from '@/utils/request'
import type {
    LoginForm,
    LoginResponse,
    ForgotForm,
    ResetPwdResponse,
    RegisterForm,
    RegisterResponse,
    CodeResponse
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
    })
}


export const sendVerifyCode = (phone: string) => {
    return request<CodeResponse>({
        url: '/user/send-code',
        method: 'POST',
        data: { phone }
    })
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
    })
}

export const registerUser = (data: Omit<RegisterForm, 'confirmPassword'>) => {
    return request<RegisterResponse>({
        url: '/user/register',
        method: 'POST',
        data: {
            username: data.username,
            realName: data.realName,
            phone: data.phone,
            code: data.code,
            userType: data.userType,
            password: data.password
        }
    })
}