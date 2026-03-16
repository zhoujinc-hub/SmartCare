import { ref } from "vue"
import { getAlerts } from "@/api/alerts"
import type {Alert} from "@/types/alert";

export function useAlerts() {

    const alerts = ref<Alert[]>([])

    const loadAlerts = async () => {
        const res = await getAlerts()
        alerts.value = res.data.data
    }

    return {
        alerts,
        loadAlerts
    }
}