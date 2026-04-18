import type { LocalUserInfo } from '@/types/user'

// Token 存储 key
const TOKEN_KEY = 'USER_TOKEN'
// 用户信息存储 key
const USER_INFO_KEY = 'USER_INFO'
// 记住用户 key
const REMEMBER_USER_KEY = 'REMEMBER_USER'

// 设置 Token
export const setToken = (token: string) => {
    localStorage.setItem(TOKEN_KEY, token)
}

// 获取 Token
export const getToken = () => {
    return localStorage.getItem(TOKEN_KEY)
}

// 设置用户信息
export const setUserInfo = (info: LocalUserInfo) => {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
}

// 获取用户信息
export const getUserInfo = (): LocalUserInfo | null => {
    const info = localStorage.getItem(USER_INFO_KEY)
    return info ? JSON.parse(info) : null
}

// 记住用户（账号+用户类型）
export const setRememberUser = (username: string, userType: number) => {
    localStorage.setItem(REMEMBER_USER_KEY, JSON.stringify({ username, userType }))
}

// 获取记住的用户信息
export const getRememberUser = () => {
    const info = localStorage.getItem(REMEMBER_USER_KEY)
    return info ? JSON.parse(info) : { username: '', userType: '' }
}

// 清除记住的用户信息
export const clearRememberUser = () => {
    localStorage.removeItem(REMEMBER_USER_KEY)
}

// 退出登录
export const logout = () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
    localStorage.removeItem(REMEMBER_USER_KEY)
}