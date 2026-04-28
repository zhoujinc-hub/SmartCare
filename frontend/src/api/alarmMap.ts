import axios from 'axios'
import type { AlarmMapPoint, UpdateAlarmStatusDTO } from '@/types/alarmMap'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000
})

export function getAlarmMapList(status?: number) {
  return request.get<AlarmMapPoint[]>('/api/alarm-map/list', {
    params: { status }
  })
}

export function updateAlarmStatus(eventId: number, data: UpdateAlarmStatusDTO) {
  return request.put<boolean>(`/api/alarm-map/${eventId}/status`, data)
}

export function pushAlarmForTest(eventId: number) {
  return request.post<boolean>(`/api/alarm-map/${eventId}/push`)
}
