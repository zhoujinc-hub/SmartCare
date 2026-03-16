import { createRouter, createWebHistory } from "vue-router"

import MainLayout from "@/layouts/MainLayout.vue"

import Dashboard from "@/views/Dashboard.vue"
import Alerts from "@/views/Alerts.vue"
import Elders from "@/views/Elders.vue"

const router = createRouter({

    history: createWebHistory(),

    routes: [
        {
            path: "/",
            component: MainLayout,
            children: [
                { path: "", component: Dashboard },
                { path: "alerts", component: Alerts },
                { path: "elders", component: Elders }
            ]
        }
    ]

})

export default router