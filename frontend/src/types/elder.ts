import type { UserItem } from './user';

/**
 * elders表结构（老人核心表）
 */
export interface ElderItem {
  elder_id: number; // 老人唯一ID（主键）
  name: string; // 姓名
  age: number; // 年龄
  gender: number; // 性别(0女1男)
  address: string; // 家庭地址
  health_notes: string; // 健康备注
  created_at: string; // 创建时间
  relatives?: string; // 家属信息（前端拼接）
  relativeCount?: number; // 家属数量
  relativeList?: UserItem[]; // 家属列表
}

/**
 * 新增/编辑老人表单数据类型
 */
export interface ElderFormData {
  elder_id: number;
  name: string;
  gender: number;
  age: number;
  address: string;
  health_notes: string;
  relativeIds: number[]; // 关联家属ID列表
}

/**
 * 老人详情数据类型
 */
export interface ElderDetailItem extends ElderItem {
  relativeList: UserItem[];
  relativeCount: number;
}

/**
 * 老人筛选参数类型
 */
export interface ElderFilterParams {
  name: string; // 老人姓名
  communityId: number | ''; // 所属社区ID（保留字段）
  healthStatus: number | ''; // 健康状态（保留字段）
}

/**
 * 分页参数类型
 */
export interface PaginationParams {
  pageNum: number;
  pageSize: number;
  total: number;
}