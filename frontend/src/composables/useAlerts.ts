// import { ref } from "vue"
// import { getAlertList } from "@/api/alertApi"
// import type { AlertLog } from "@/types/alertType"
//
// export function useAlerts() {
//   const alerts = ref<AlertLog[]>([])
//
//   const loadAlerts = async () => {
//     const res = await getAlertList({ page: 1, size: 100 })
//     alerts.value = res.data?.list || []   // 这里修复！
//   }
//
//   return {
//     alerts,
//     loadAlerts
//   }
// }