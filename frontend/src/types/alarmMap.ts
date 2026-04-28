export interface AlarmMapPoint {
  eventId: number
  cameraId: number
  elderId?: number
  elderName?: string
  cameraName?: string
  locationDesc?: string
  latitude: number
  longitude: number
  fallTime?: string
  detectTime?: string
  screenshotPath?: string
  videoPath?: string
  confidence?: number
  status: number
  sendStatus?: number
  sendMethod?: number
  sentAt?: string
}

export interface UpdateAlarmStatusDTO {
  status: number
  processedBy?: number
  processNotes?: string
}

export interface AlarmWsMessage {
  type: 'CONNECTED' | 'NEW_ALARM' | 'ALARM_STATUS_CHANGED'
  message?: string
  eventId?: number
  status?: number
  data?: AlarmMapPoint
}
