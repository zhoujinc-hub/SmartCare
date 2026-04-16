import js from '@eslint/js'
import vue from 'eslint-plugin-vue'
import vueParser from 'vue-eslint-parser'
import tsParser from '@typescript-eslint/parser'
import tsPlugin from '@typescript-eslint/eslint-plugin'
// 新增：导入 globals 包（需要先安装）
import globals from 'globals'

export default [
  js.configs.recommended,
  ...vue.configs['flat/recommended'],
  {
    files: ['**/*.ts', '**/*.tsx', '**/*.vue'],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        parser: tsParser,
        ecmaVersion: 'latest',
        sourceType: 'module'
      },
      // ✅ 新增：添加浏览器 + Node 全局变量
      globals: {
        ...globals.browser, // 识别 localStorage/console/window 等浏览器 API
        ...globals.node,    // 保留 Node 环境变量（如 require/module）
        ...globals.es2021   // 识别 ES2021 全局变量
      }
    },
    plugins: {
      '@typescript-eslint': tsPlugin
    },
    rules: {
          // 禁用单行 HTML 元素内容换行检查
      'vue/singleline-html-element-content-newline': 'off',
            // 放宽多行 HTML 元素内容换行检查
      'vue/multiline-html-element-content-newline': ['warn', {
        ignoreWhenEmpty: true,
        ignores: ['pre', 'textarea', 'template']
      }],
      'vue/multi-word-component-names': 'off',
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
      // ✅ 可选：关闭不必要的警告（按需添加）
      'no-console': 'off',        // 允许 console 打印
      'vue/attributes-order': 'off', // 关闭属性顺序警告
      'vue/max-attributes-per-line': 'off', // 关闭属性换行警告
      'vue/html-self-closing': 'off', // 关闭组件自闭合警告
      'vue/no-unused-vars': 'off',      // 模板中未使用变量
      'vue/no-unused-properties': 'off' // 未使用的 props/data（如果不需要可以删除）

    }
  },
  {
    ignores: ['dist', 'node_modules']
  }
]