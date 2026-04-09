/**
 * cameras表结构（摄像头核心表）
 */
export interface CameraItem {
  camera_id: number; // 摄像头唯一ID（主键）
  camera_type: number; // 0:家庭 1:社区
  camera_name: string; // 设备名称
  device_serial: string; // 设备序列号（唯一）
  stream_url: string; // 视频流地址
  location_desc: string; // 安装位置描述
  latitude: number | string; // 纬度
  longitude: number | string; // 经度
  status: number; // 0:离线 1:在线
  created_at: string; // 创建时间
  
  // 扩展字段（非数据库表字段，前端拼接/扩展）
  householdId?: number; // 所属家庭ID
  householdName?: string; // 所属家庭名称
  householdAddress?: string; // 家庭地址
  lastHeartbeat?: string; // 最后心跳时间
  totalRuntime?: string; // 累计运行时长
  todayAlertCount?: number; // 今日报警次数
  totalAlertCount?: number; // 累计报警次数
  lastAlertTime?: string; // 最后报警时间
  firmwareVersion?: string; // 固件版本
  updatedAt?: string; // 最后更新时间
  createdBy?: string; // 创建人
  updatedBy?: string; // 最后更新人
  remark?: string; // 备注信息
}

/**
 * 家庭信息扩展接口（用于关联选择）
 */
export interface HouseholdItem {
  householdId: number;
  householdName: string;
  address: string;
}

/**
 * 新增/编辑摄像头表单数据类型
 */
export interface CameraFormData {
  camera_id: number;
  camera_type: number;
  camera_name: string;
  device_serial: string;
  stream_url: string;
  location_desc: string;
  latitude: number | string;
  longitude: number | string;
  status: number;
  householdId: number;
  remark: string;
}

/**
 * 分页接口返回类型
 */
export interface PageResponse<T> {
  code: number;
  message: string;
  data: {
    list: T[];
    total: number;
    pageNum: number;
    pageSize: number;
  };
}

/**
 * 基础接口返回类型
 */
export interface BaseResponse {
  code: number;
  message: string;
  data?: any;
}