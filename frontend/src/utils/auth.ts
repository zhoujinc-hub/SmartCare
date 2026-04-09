import type { LocalUserInfo } from '../types/user'

/**
 * 存储Token到本地
 * @param token 登录令牌
 */
export const setToken = (token: string) => {
  localStorage.setItem('token', token)
}

/**
 * 获取本地Token
 */
export const getToken = () => {
  return localStorage.getItem('token') || ''
}

/**
 * 存储用户信息到本地
 * @param userInfo 用户信息
 */
export const setUserInfo = (userInfo: LocalUserInfo) => {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

/**
 * 获取本地用户信息
 */
export const getUserInfo = (): LocalUserInfo => {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : {}
}

/**
 * 存储记住的用户信息
 * @param username 账号
 * @param userType 用户类型
 */
export const setRememberUser = (username: string, userType: number) => {
  localStorage.setItem('rememberUser', JSON.stringify({ username, userType }))
}

/**
 * 获取记住的用户信息
 */
export const getRememberUser = () => {
  const user = localStorage.getItem('rememberUser')
  return user ? JSON.parse(user) : { username: '', userType: 2 }
}

/**
 * 清除记住的用户信息
 */
export const clearRememberUser = () => {
  localStorage.removeItem('rememberUser')
}

/**
 * 退出登录（清空本地存储）
 */
export const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('rememberUser')
}