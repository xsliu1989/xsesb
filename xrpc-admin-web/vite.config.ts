import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  server: {
    port: 9520,
    // 不允许自动跳到 9521（那是 xrpc-admin 的端口，跳过去会造成代理自环）
    strictPort: true,
    proxy: {
      '/admin-api': {
        target: 'http://localhost:9521',
        changeOrigin: true
      }
    }
  }
})
