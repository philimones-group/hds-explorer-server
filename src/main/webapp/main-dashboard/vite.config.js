import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        entryFileNames: '[name]-bundle-dashboard.js', // Customize the JS bundle filename
        chunkFileNames: '[name]-bundle-dashboard.js', // Customize the JS chunk filename
        assetFileNames: '[name]-bundle-dashboard.[ext]', // Customize the asset filename (e.g., CSS)

        // Set the publicPath if your app will be served from a subdirectory
        // publicPath: '/your-subdirectory/'
      }
    }
  }
})
