/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    test: {
        environment: 'jsdom',
        coverage: {
            reporter: ['text', 'lcov']
        }
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8089',
                changeOrigin: true,
            }
        }
    }
})