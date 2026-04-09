import request from '../utils/request';
import type { 
  HouseholdItem, 
  HouseholdFormData, 
  HouseholdFilterParams 
} from '../types/household';

// 本地定义分页（无外部依赖）
interface PaginationParams {
  pageNum: number;
  pageSize: number;
}

/**
 * 获取家庭列表（分页+筛选）
 */
export const getHouseholdList = async (
  pagination: PaginationParams,
  filterParams: HouseholdFilterParams
): Promise<{ code: number; data: { records: HouseholdItem[]; total: number }; message: string }> => {
  const params = {
    ...pagination,
    ...filterParams
  };
  return request({
    url: '/api/households/list',
    method: 'GET',
    params
  });
};

/**
 * 获取家庭详情
 */
export const getHouseholdDetail = async (householdId: number): Promise<{ code: number; data: HouseholdItem; message: string }> => {
  return request({
    url: `/api/households/${householdId}`,
    method: 'GET'
  });
};

/**
 * 新增家庭
 */
export const addHousehold = async (formData: HouseholdFormData): Promise<{ code: number; message: string }> => {
  return request({
    url: '/api/households',
    method: 'POST',
    data: formData
  });
};

/**
 * 编辑家庭
 */
export const updateHousehold = async (
  householdId: number,
  formData: HouseholdFormData
): Promise<{ code: number; message: string }> => {
  return request({
    url: `/api/households/${householdId}`,
    method: 'PUT',
    data: formData
  });
};

/**
 * 删除家庭
 */
export const deleteHousehold = async (householdId: number): Promise<{ code: number; message: string }> => {
  return request({
    url: `/api/households/${householdId}`,
    method: 'DELETE'
  });
};