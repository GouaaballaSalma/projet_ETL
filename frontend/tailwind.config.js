/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cfg: {
          green: '#00583A', // CFG logo color approx
          gold: '#C59A45', // CFG gold approx
          light: '#F4F5F7',
          dark: '#1A202C'
        }
      }
    },
  },
  plugins: [],
}
