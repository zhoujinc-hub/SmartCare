import request from "./request"
import type { Alert } from "@/types/alert"
import type {ApiResponse} from "@/types/api"

export function getAlerts() {
    return request.get<ApiResponse<Alert[]>>("/api/alerts")
}