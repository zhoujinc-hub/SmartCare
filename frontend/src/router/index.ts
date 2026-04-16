import { createRouter, createWebHistory } from "vue-router";
// 导入 MainLayout 和页面组件
import MainLayout from "@/layout/MainLayout.vue";
import Dashboard from "@/views/Dashboard.vue";
import Alerts from "@/views/Alerts.vue";
import Elders from "@/views/Elders.vue";
// 新增：导入登录页
import Login from "@/views/login.vue";
import Households from '@/views/Households.vue'
import Cameras from '@/views/Cameras.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 登录页路由（独立布局，不包含侧边栏）
    {
      path: "/login",
      name: "Login",
      component: Login
    },
    // 主页面路由（包含侧边栏布局）
    {
      path: "/",
      component: MainLayout,
      redirect: "/dashboard", // 重定向到仪表盘
      children: [
        { path: "dashboard", component: Dashboard },
        { path: "alerts", component: Alerts },
        { path: "elders", component: Elders },
        { path: "households", component: Households },
        { path: "cameras", component: Cameras },
      ]
    }
  ]
});

export default router;