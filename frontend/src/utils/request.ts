import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

const instance: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token')
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// 响应拦截器 - 解包 response.data
instance.interceptors.response.use(
    (response: AxiosResponse) => {
        // 直接返回后端的数据结构 { code, message, data }
        return response.data
    },
    (error) => {
        ElMessage.error(error.response?.data?.message || '请求失败')
        return Promise.reject(error)
    }
)

export default instance
