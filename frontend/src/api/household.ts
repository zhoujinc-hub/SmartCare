import request from '../utils/request';
import type { 
  HouseholdItem, 
  HouseholdFormData, 
  HouseholdFilterParams, 
  PaginationParams,
  BatchThresholdForm,
  CommunityItem
} from '../types/household';

/**
 * 获取社区列表
 * @returns 社区列表数据
 */
export const getCommunityList = async (): Promise<{ code: number; data: CommunityItem[]; message: string }> => {
  return request({
    url: '/api/communities',
    method: 'GET'
  });
};

/**
 * 获取家庭列表（分页+筛选）
 * @param pagination 分页参数
 * @param filterParams 筛选参数
 * @returns 家庭列表分页数据
 */
export const getHouseholdList = async (
  pagination: Pick<PaginationParams, 'pageNum' | 'pageSize'>,
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
 * @param householdId 家庭ID
 * @returns 家庭详情数据
 */
export const getHouseholdDetail = async (householdId: number): Promise<{ code: number; data: HouseholdItem; message: string }> => {
  return request({
    url: `/api/households/${householdId}`,
    method: 'GET'
  });
};

/**
 * 新增家庭
 * @param formData 表单数据
 * @returns 新增结果
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
 * @param householdId 家庭ID
 * @param formData 表单数据
 * @returns 编辑结果
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
 * @param householdId 家庭ID
 * @returns 删除结果
 */
export const deleteHousehold = async (householdId: number): Promise<{ code: number; message: string }> => {
  return request({
    url: `/api/households/${householdId}`,
    method: 'DELETE'
  });
};

/**
 * 批量更新报警阈值
 * @param householdIds 家庭ID列表
 * @param threshold 报警阈值
 * @returns 更新结果
 */
export const batchUpdateThreshold = async (
  householdIds: number[],
  threshold: number
): Promise<{ code: number; message: string }> => {
  return request({
    url: '/api/households/batch-update-threshold',
    method: 'POST',
    data: {
      householdIds,
      alertThresholdSeconds: threshold
    }
  });
};