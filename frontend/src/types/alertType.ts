/** 用户类型（关联users表） */
export interface User {
  user_id: bigint;
  username: string;
  real_name: string | null;
  phone: string;
  user_type: 1 | 2; // 1:管理员 2:家属
  status: 0 | 1; // 0:禁用 1:启用
  created_at: string;
}

/** 摄像头类型（关联cameras表） */
export interface Camera {
  camera_id: bigint;
  camera_type: 0 | 1; // 0:家庭 1:社区
  camera_name: string;
  device_serial: string;
  stream_url: string | null;
  location_desc: string | null;
  latitude: number | null;
  longitude: number | null;
  status: 0 | 1; // 0:离线 1:在线
  created_at: string;
}

/** 摔倒事件类型（关联fall_events表） */
export interface FallEvent {
  event_id: bigint;
  camera_id: bigint;
  elder_id: bigint | null;
  elder_name: string | null;
  is_registered: 0 | 1; // 0:未注册 1:已注册
  fall_time: string;
  detect_time: string;
  video_path: string | null;
  screenshot_path: string | null;
  confidence: number | null; // AI置信度 0-1
  status: 1 | 2 | 3; // 1:待处理 2:已处理 3:误报
  processed_by: bigint | null;
  processed_at: string | null;
  process_notes: string | null;
  created_at: string;
  // 关联字段（接口返回时拼接）
  camera?: Camera;
  processor?: User; // 处理人信息
}

/** 告警日志类型（核心，关联alert_logs表） */
export interface AlertLog {
  alert_id: bigint;
  event_id: bigint;
  recipient_id: bigint;
  recipient_type: 1 | 2; // 1:家属 2:管理员
  recipient_phone: string;
  send_method: 1 | 2 | 3; // 1:短信 2:APP推送 3:电话
  send_status: 0 | 1 | 2; // 0:失败 1:成功 2:发送中
  error_msg: string | null;
  sent_at: string | null;
  created_at: string;
  // 关联字段（接口返回时拼接）
  fall_event?: FallEvent;
  recipient?: User; // 接收人信息
}

/** 告警列表查询参数 */
export interface AlertQueryParams {
  page: number;
  size: number;
  send_status?: 0 | 1 | 2; // 发送状态筛选
  send_method?: 1 | 2 | 3; // 发送方式筛选
  recipient_type?: 1 | 2; // 接收人类型筛选
  start_time?: string; // 发送开始时间
  end_time?: string; // 发送结束时间
}

/** 告警列表返回结果 */
export interface AlertPageResult {
  list: AlertLog[];
  total: number;
  page: number;
  size: number;
}

/** 处理告警参数（更新状态/备注） */
export interface HandleAlertParams {
  alert_id: bigint;
  status: 1 | 2 | 3; // 摔倒事件状态
  process_notes?: string;
}