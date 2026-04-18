// 跌倒事件查询参数
export interface FallEventQueryParams {
    elderName?: string
    status?: number
    cameraId?: number
    pageNum: number
    pageSize: number
}

// 处理跌倒事件的请求体
export interface FallEventHandleData {
    processedBy?: number
    status?: number
}

// 保存处理备注的请求体
export interface FallEventNotesData {
    processNotes: string
}

// 跌倒事件列表项（可选，给组件使用）
export interface FallEventItem {
    eventId: number
    cameraId: number
    elderId?: number
    elderName?: string
    isRegistered: 0 | 1
    fallTime: string
    detectTime: string
    videoPath?: string
    screenshotPath?: string
    confidence?: number
    status: 1 | 2 | 3 // 1:待处理 2:已处理 3:误报
    processedBy?: number
    processedAt?: string
    processNotes?: string
}

// 列表接口响应结构
export interface FallEventListResponse {
    code: number
    message: string
    data: {
        list: FallEventItem[]
        total: number
    }
}