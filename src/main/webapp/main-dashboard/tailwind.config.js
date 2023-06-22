/** @type {import('tailwindcss').Config} */
export default {
  content: [
      "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors:{
        "main":"#F0F2F5",
        "secondary":"#FFFFFF",
        "sidebar":"#313131",
        "sidebar-hover":"#626262",
        "sidebar-clicked":"#616161",
        "btn-clicked":"#626262",
        "dark-mode":"#202124",
        "gray-shadow":"#DDDDDD",
        "card-label":"#9A9A9A",
        "card-value":"#333333",
        "title":"#333333",
        "title-alt":"#344767",
        "sub-title":"#9A9A9A",
        "text-color":"#344771",
        "outline":"#DFDFDF",
        "costume-green":"#5FB563",
        "costume-blue":"#3189EC",
        "costume-black":"#333338",
        "costume-dark":"#1A2035",
        "costume-pink":"#DD2567",
      },
    },
  },
  plugins: [],
}

