/**
 * 社区信息接口 (对应 communities 表)
 */
export interface CommunityItem {
  communityId: number;                // 社区ID (communities.community_id)
  communityName: string;              // 社区名称 (communities.community_name)
  address: string;                    // 社区地址 (communities.address)
  contactPhone: string;               // 社区联系电话 (communities.contact_phone)
  alertThresholdSeconds: number;      // 社区默认报警阈值 (communities.alert_threshold_seconds)
}

/**
 * 老人简易信息接口 (关联 elders 表核心字段)
 */
export interface ElderMiniItem {
  elderId: number;                    // 老人ID (elders.elder_id)
  name: string;                       // 老人姓名 (elders.name)
  age: number;                        // 老人年龄 (elders.age)
  gender?: number;                    // 老人性别 (elders.gender: 0=女 1=男)
  healthStatusText: string;           // 健康状态文本（前端展示用）
  emergencyPhone: string;             // 紧急联系电话
  healthNotes?: string;               // 健康备注 (elders.health_notes)
}

/**
 * 摄像头简易信息接口 (对应 cameras 表核心字段)
 */
export interface CameraMiniItem {
  cameraId: number;                   // 摄像头ID (cameras.camera_id)
  cameraName: string;                 // 摄像头名称 (cameras.camera_name)
  locationDesc: string;               // 安装位置描述 (cameras.location_desc)
  deviceSerial: string;               // 设备序列号 (cameras.device_serial)
  status: number;                     // 设备状态 (cameras.status: 0=离线 1=在线)
  statusText: string;                 // 状态文本（前端展示用）
  cameraType?: number;                // 摄像头类型 (cameras.camera_type: 0=家庭 1=社区)
  streamUrl?: string;                 // 流地址 (cameras.stream_url)
}

/**
 * 家庭信息接口 (对应 households 表 + 关联表字段)
 */
export interface HouseholdItem {
  householdId: number;                // 家庭ID (households.household_id)
  householdName: string;              // 户主姓名 (households.household_name)
  communityId: number;                // 所属社区ID (households.community_id)
  communityName: string;              // 社区名称（关联查询）
  address: string;                    // 详细地址 (households.address)
  alertThresholdSeconds: number;      // 报警阈值(秒) (households.alert_threshold_seconds)
  emergencyContactName: string;       // 紧急联系人姓名 (households.emergency_contact_name)
  emergencyContactPhone: string;      // 紧急联系电话 (households.emergency_contact_phone)
  isActive: number;                   // 状态 (households.is_active: 1=启用 0=禁用)
  remark: string;                     // 备注 (households.remark)
  elderList: ElderMiniItem[];         // 关联老人列表（多对多）
  cameraList: CameraMiniItem[];       // 绑定摄像头列表（多对多）
  communityContactPhone: string;      // 社区联系电话（关联查询）
  createdAt: string;                  // 创建时间 (households.created_at)
  updatedAt: string;                  // 更新时间 (households.updated_at)
  createdBy: string;                  // 创建人 (households.created_by)
  updatedBy: string;                  // 更新人 (households.updated_by)
}

/**
 * 家庭表单数据类型（新增/编辑）
 */
export type HouseholdFormData = Omit<
  HouseholdItem, 
  'householdId' | 'communityName' | 'elderList' | 'cameraList' | 
  'communityContactPhone' | 'createdAt' | 'updatedAt' | 'createdBy' | 'updatedBy'
>;

/**
 * 家庭列表筛选参数类型
 */
export interface HouseholdFilterParams {
  householdName: string;
  communityId?: number;
  hasCamera?: number;
}

/**
 * 分页参数类型
 */
export interface PaginationParams {
  pageNum: number;
  pageSize: number;
  total: number;
}

/**
 * 批量设置阈值表单类型
 */
export interface BatchThresholdForm {
  alertThresholdSeconds: number;
}