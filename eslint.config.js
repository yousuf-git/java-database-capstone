// Core ESLint rules only, so CI needs no plugin packages beyond eslint itself.
const browserGlobals = {
  alert: "readonly",
  confirm: "readonly",
  console: "readonly",
  document: "readonly",
  fetch: "readonly",
  localStorage: "readonly",
  setTimeout: "readonly",
  URLSearchParams: "readonly",
  window: "readonly"
};

// Attached to window by the page module that owns them, then called from other modules.
const appGlobals = {
  adminAddDoctor: "readonly",
  adminLoginHandler: "readonly",
  doctorLoginHandler: "readonly",
  loginPatient: "readonly",
  selectRole: "readonly",
  signupPatient: "readonly"
};

export default [
  {
    files: ["app/src/main/resources/static/js/**/*.js"],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: "module",
      globals: { ...browserGlobals, ...appGlobals }
    },
    rules: {
      "no-undef": "error",
      "no-unused-vars": ["error", { args: "none" }],
      "no-dupe-keys": "error",
      "no-dupe-args": "error",
      "no-unreachable": "error",
      "no-const-assign": "error",
      "no-redeclare": "error",
      eqeqeq: ["warn", "smart"]
    }
  },
  {
    // These files are loaded with a plain <script> tag and share globals across files.
    files: ["app/src/main/resources/static/js/util.js", "app/src/main/resources/static/js/render.js",
      "app/src/main/resources/static/js/components/header.js",
      "app/src/main/resources/static/js/components/footer.js"],
    languageOptions: { sourceType: "script" },
    rules: { "no-unused-vars": "off", "no-undef": "off", "no-redeclare": "off" }
  }
];
