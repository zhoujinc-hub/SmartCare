// import { defineStore } from "pinia"
// import { getAlerts } from "@/api/alerts"
// import type { Alert } from "@/types/alert"
//
// export const useAlertStore = defineStore("alerts", {
//
//     state: () => ({
//         alerts: [] as Alert[],
//     }),
//
//     actions: {
//         async fetchAlerts() {
//             const res = await getAlerts()
//             this.alerts = res as Alert[]
//         }
//     }
//
// })