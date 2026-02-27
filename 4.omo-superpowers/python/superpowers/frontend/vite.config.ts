import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: [
      'code.aboydfd.com',
      '.code.aboydfd.com',
      'localhost'
    ],

    proxy: {
      '/api': 'http://localhost:8000'
    }
  }
})
