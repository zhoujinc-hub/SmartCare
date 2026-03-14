import { ref } from "vue"
import { getAlerts } from "@/api/alerts"

export function useAlerts() {

    const alerts = ref([])

    const loadAlerts = async () => {
        const res = await getAlerts()
        alerts.value = res.data
    }

    return {
        alerts,
        loadAlerts
    }
}