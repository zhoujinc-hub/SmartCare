/**
 * 老人信息（匹配 elders 表）
 */
export interface Elder {
    elderId?: number;
    name: string;         // 数据库：name
    age?: number;
    gender?: 0 | 1;       // 0女 1男
    address?: string;
    healthNotes?: string;
    createdAt?: string;
}

/**
 * 摔倒事件（匹配 fall_events 表）
 */
export interface ElderFallEvent {
    eventId?: number;
    cameraId?: number;
    elderId?: number;
    elderName?: string;
    isRegistered?: 0 | 1;
    fallTime: string;
    detectTime: string;
    videoPath?: string;
    screenshotPath?: string;
    confidence?: number;
    status: 1 | 2 | 3; // 1待处理 2已处理 3误报
    processedBy?: number;
    processedAt?: string;
    processNotes?: string;
    createdAt?: string;
}

/**
 * 分页参数
 */
export interface PageQuery {
    pageNum: number;
    pageSize: number;
}