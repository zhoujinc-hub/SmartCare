import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Dashboard from "@/views/Dashboard.vue";
import Alerts from "@/views/Alerts.vue";
import Elders from "@/views/Elders.vue";
import Cameras from "@/views/Cameras.vue";
import Login from "@/views/login.vue";
import FallEvent from "@/views/fallEvent.vue";

// ⭐ 正确路径（关键）
import AlarmMap from "@/views/AlarmMap.vue";

const UserWeb = () => import('@/views/userweb.vue')

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: "/",
            redirect: "/login"
        },
        {
            path: "/login",
            name: "Login",
            component: Login
        },
        {
            path: "/admin",
            component: MainLayout,
            redirect: "/admin/dashboard",
            children: [
                { path: "dashboard", component: Dashboard },
                { path: "alerts", component: Alerts },
                { path: "elders", component: Elders },
                { path: "cameras", component: Cameras },
                { path: "fallEvent", component: FallEvent },

                // ⭐ 报警地图
                { path: "alarm/map", component: AlarmMap }
            ]
        },
        {
            path: "/userweb",
            name: "UserWeb",
            component: UserWeb
        },
        {
            path: "/:pathMatch(.*)*",
            redirect: "/login"
        }
    ]
});

export default router;