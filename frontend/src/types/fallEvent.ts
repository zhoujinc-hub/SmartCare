// 查询参数
export interface FallEventQueryParams {
    elderName?: string
    status?: number
    cameraId?: number
    pageNum?: number
    pageSize?: number
}

// 处理事件
export interface FallEventHandleDto {
    processedBy?: number
}

// 保存备注
export interface FallEventNotesDto {
    processNotes?: string
}

// 列表项
export interface FallEventVo {
    eventId: number
    cameraId: number
    elderId?: number
    elderName?: string
    isRegistered: number
    fallTime: string
    detectTime: string
    videoPath?: string
    screenshotPath?: string
    confidence?: number
    status: number
    processedBy?: number
    processedAt?: string
    processNotes?: string
}

// 分页响应
export interface PageVo<T> {
    total: number
    pages: number
    pageNum: number
    pageSize: number
    records: T[]
}

// 列表响应
export interface FallEventListResponse {
    code: number
    message: string
    data: PageVo<FallEventVo>
}

// 详情
export interface FallEventDetailResponse {
    code: number
    message: string
    data: FallEventVo
}

// 通用
export interface FallEventBaseResponse {
    code: number
    message: string
    data?: any
}