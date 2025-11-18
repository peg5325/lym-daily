/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#4A90E2',
        'hero-blue': '#1E3A8A',
      },
      fontFamily: {
        'sans': ['Noto Sans KR', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
