import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000
})

// 泛型请求函数，支持指定返回值类型
const request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  return new Promise((resolve, reject) => {
    service(config)
      .then((response: AxiosResponse<T>) => {
        resolve(response.data)
      })
      .catch((error) => {
        reject(error)
      })
  })
}

export default request