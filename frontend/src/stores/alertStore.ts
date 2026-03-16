import { defineStore } from "pinia"
import { getAlerts } from "@/api/alerts"

export const useAlertStore = defineStore("alerts", {

    state: () => ({
        alerts: []
    }),

    actions: {
        async fetchAlerts() {
            const res = await getAlerts()
            this.alerts = res.data
        }
    }

})