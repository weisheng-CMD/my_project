import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // 代理 API 请求到后端 8080，解决跨域问题
    proxy: {
      '/tickets': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/login': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/register': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
