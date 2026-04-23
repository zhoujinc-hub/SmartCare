import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Dashboard from "@/views/Dashboard.vue";
import Alerts from "@/views/Alerts.vue";
import Elders from "@/views/Elders.vue";
import Cameras from "@/views/Cameras.vue";
import Login from "@/views/login.vue";
import FallEvent from "@/views/fallEvent.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 登录页
        {
            path: "/login",
            name: "Login",
            component: Login
        },

        // 管理员布局（所有页面都在这里）
        {
            path: "/",
            component: MainLayout,
            redirect: "/dashboard",
            children: [
                { path: "dashboard", component: Dashboard },
                { path: "alerts", component: Alerts },
                { path: "elders", component: Elders },
                { path: "cameras", component: Cameras },
                { path: "fallEvent", component: FallEvent }
            ]
        }
    ]
});



export default router;