export interface Elder {
    elderId?: number
    userId?: number
    name: string
    age?: number
    gender?: 0 | 1
    address?: string
    healthNotes?: string
    createdAt?: string
}

export interface ElderFallEvent {
    eventId?: number
    cameraId?: number
    elderId?: number
    userId?: number
    elderName?: string
    isRegistered?: 0 | 1
    fallTime: string
    detectTime: string
    locationDesc?: string
    videoPath?: string
    screenshotPath?: string
    confidence?: number
    status: 1 | 2 | 3
    processedBy?: number
    processedAt?: string
    processNotes?: string
    createdAt?: string
}

export interface PageQuery {
    pageNum: number
    pageSize: number
}