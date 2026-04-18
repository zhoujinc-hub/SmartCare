import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Dashboard from "@/views/Dashboard.vue";
import Alerts from "@/views/Alerts.vue";
import Elders from "@/views/Elders.vue";
import Cameras from "@/views/Cameras.vue";
import Login from "@/views/login.vue";
import FallEvent from "@/views/fallEvent.vue";
import { getToken, getUserInfo } from '@/utils/auth'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 登录页
        {
            path: "/login",
            name: "Login",
            component: Login
        },
        // 家属专属页面
        {
            path: "/fallEvent",
            name: "FallEvent",
            component: FallEvent,
            meta: { role: 2 }
        },
        // 管理员布局
        {
            path: "/",
            component: MainLayout,
            redirect: "/dashboard",
            meta: { role: 1 },
            children: [
                { path: "dashboard", component: Dashboard },
                { path: "alerts", component: Alerts },
                { path: "elders", component: Elders },
                { path: "cameras", component: Cameras }
            ]
        }
    ]
})

// 路由守卫
/*router.beforeEach((to, from, next) => {
    const token = getToken()
    const userInfo = getUserInfo()
    const userType = userInfo?.userType

    // 未登录，跳登录页
    if (!token && to.path !== '/login') {
        return next('/login')
    }

    // 已登录，禁止访问登录页
    if (token && to.path === '/login') {
        return next(userType === 1 ? '/dashboard' : '/fallEvent')
    }

    // 权限控制
    if (token && userType) {
        // 管理员不能访问家属页面
        if (userType === 1 && to.meta.role === 2) {
            return next('/dashboard')
        }
        // 家属不能访问管理员页面
        if (userType === 2 && to.meta.role === 1) {
            return next('/fallEvent')
        }
    }

    next()
})*/

export default router