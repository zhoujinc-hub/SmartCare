import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src')
        }
    },
    server: {
        // 关键：配置代理
        proxy: {
            '/api': {
                target: 'http://114.215.169.226:8080', // 你的后端地址
                changeOrigin: true, // 改变请求源，解决跨域
                rewrite: (path) => path.replace(/^\/api/, '/api') // 路径重写，保持 /api 前缀
            }
        }
    }
})