import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    // ✅ 配置 @ 指向 src 目录
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173
  }
})