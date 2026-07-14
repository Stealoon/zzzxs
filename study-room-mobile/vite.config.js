import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5173,
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
