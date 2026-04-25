import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Dashboard from "@/views/Dashboard.vue";
import Alerts from "@/views/Alerts.vue";
import Elders from "@/views/Elders.vue";
import Cameras from "@/views/Cameras.vue";
import Login from "@/views/login.vue";
import FallEvent from "@/views/fallEvent.vue";

// 先创建一个占位的家属页面（后续再开发）
const UserWeb = () => import('@/views/userweb.vue')

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 登录页设为默认启动页
        {
            path: "/",
            redirect: "/login"
        },
        {
            path: "/login",
            name: "Login",
            component: Login
        },

        // 管理员布局（所有管理页面）
        {
            path: "/admin",
            component: MainLayout,
            redirect: "/admin/dashboard",
            children: [
                { path: "dashboard", component: Dashboard },
                { path: "alerts", component: Alerts },
                { path: "elders", component: Elders },
                { path: "cameras", component: Cameras },
                { path: "fallEvent", component: FallEvent }
            ]
        },

        // 家属页面（占位，后续开发）
        {
            path: "/userweb",
            name: "UserWeb",
            component: UserWeb
        },

        // 404兜底：所有未匹配的路径都跳转到登录页或404页面
        {
            path: "/:pathMatch(.*)*",
            redirect: "/login"
        }
    ]
});

export default router;