/** 用户类型（关联users表） */
export interface User {
    userId: bigint;
    username: string;
    realName: string | null;
    phone: string;
    userType: 1 | 2; // 1:管理员 2:家属
    status: 0 | 1; // 0:禁用 1:启用
    createdAt: string;
}

/** 摄像头类型（关联cameras表） */
export interface Camera {
    cameraId: bigint;
    cameraType: 0 | 1; // 0:家庭 1:社区
    cameraName: string;
    deviceSerial: string;
    streamUrl: string | null;
    locationDesc: string | null;
    latitude: number | null;
    longitude: number | null;
    status: 0 | 1; // 0:离线 1:在线
    createdAt: string;
}

/** 摔倒事件类型（关联fall_events表） */
export interface FallEvent {
    eventId: bigint;
    cameraId: bigint;
    elderId: bigint | null;
    elderName: string | null;
    isRegistered: 0 | 1; // 0:未注册 1:已注册
    fallTime: string;
    detectTime: string;
    videoPath: string | null;
    screenshotPath: string | null;
    confidence: number | null; // AI置信度 0-1
    status: 1 | 2 | 3; // 1:待处理 2:已处理 3:误报
    processedBy: bigint | null;
    processedAt: string | null;
    processNotes: string | null;
    createdAt: string;
    camera?: Camera;
    processor?: User;
}

/** 告警日志类型（核心） */
export interface AlertLog {
    alertId: bigint;
    eventId: bigint;
    recipientId: bigint;
    recipientType: 1 | 2;
    recipientPhone: string;
    sendMethod: 1 | 2 | 3;
    sendStatus: 0 | 1 | 2;
    errorMsg: string | null;
    sentAt: string | null;
    fallEvent?: FallEvent;
    recipient?: User;
}

/** 告警列表查询参数 */
export interface AlertQueryParams {
    page?: number;
    pageNum: number;
    pageSize: number;
    sendStatus?: 0 | 1 | 2;
    sendMethod?: 1 | 2 | 3;
    recipientType?: 1 | 2;
    startTime?: string;
    endTime?: string;
}

/** 告警列表返回结果 */
export interface AlertPageResult {
    total: number;
    pages: number;
    pageNum: number;
    pageSize: number;
    records: AlertLog[];
}

export interface AlertPageResponse {
    code: number;
    message: string;
    data: AlertPageResult;
}

/** 处理告警参数 */
export interface HandleAlertParams {
    alertId: bigint;
    sendStatus: 0 | 1 | 2;
    errorMsg: string;
}