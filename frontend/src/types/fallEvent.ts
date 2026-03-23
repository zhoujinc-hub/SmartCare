/** 告警列表项类型（对齐数据库表结构） */
export interface AlertItem {
  // fall_events 表核心字段
  eventId: number;                // 事件ID (fall_events.event_id)
  cameraId: number;               // 摄像头ID (fall_events.camera_id)
  cameraType: number;             // 摄像头类型（0:家庭 1:社区，关联 cameras.camera_type）
  elderId?: number;               // 老人ID (fall_events.elder_id)
  elderName: string;              // 老人姓名 (fall_events.elder_name)
  isRegistered: number;           // 是否注册 (fall_events.is_registered: 0=未 1=已)
  fallTime: string;               // 摔倒发生时间 (fall_events.fall_time)
  detectTime: string;             // 系统检测时间 (fall_events.detect_time)
  confidence: number;             // AI置信度 (fall_events.confidence: 0-1)
  status: number;                 // 处理状态 (fall_events.status: 1=待处理 2=已处理 3=误报)
  processedBy?: number;           // 处理人ID (fall_events.processed_by，关联 users.user_id)
  processedByName?: string;       // 处理人姓名（关联 users.real_name）
  processedAt?: string;           // 处理时间 (fall_events.processed_at)
  processNotes?: string;          // 处理备注 (fall_events.process_notes)
  // cameras 表关联字段
  locationDesc: string;           // 告警位置 (cameras.location_desc)
  // alert_logs 表关联字段
  alertSent: boolean;             // 是否通知成功（根据 alert_logs.send_status 判断）
}

/** 告警筛选参数类型 */
export interface AlertFilterParams {
  eventType: string | number;     // 0:家庭 1:社区
  status: string | number;        // 1:待处理 2:已处理 3:误报
  dateRange: string[];            // 时间范围 [开始, 结束]
}

/** 分页参数类型 */
export interface PaginationParams {
  pageNum: number;
  pageSize: number;
  total: number;
}

/** 告警列表接口返回类型 */
export interface AlertListResponse {
  code: number;
  message: string;
  data: {
    list: AlertItem[];
    total: number;
  };
}

/** 基础接口返回类型 */
export interface BaseResponse {
  code: number;
  message: string;
  data?: any;
}