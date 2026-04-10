import request from '../utils/request';
import type { 
  ElderItem, 
  ElderFormData, 
  ElderDetailItem, 
  ElderFilterParams, 
  PaginationParams 
} from '../types/elder';
import type { 
  PageResponse, 
  BaseResponse, 
  RelationItem 
} from '../types/user';

/**
 * 获取老人列表（分页+筛选）
 * @param pagination 分页参数
 * @param filterParams 筛选参数
 * @returns 老人列表数据
 */
export const getElderList = async (
  pagination: Pick<PaginationParams, 'pageNum' | 'pageSize'>,
  filterParams: ElderFilterParams
): Promise<PageResponse<ElderItem>> => {
  const params = {
    name: filterParams.name,
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  };

  return request({
    url: '/api/elder/list',
    method: 'GET',
    params
  });
};

/**
 * 新增老人信息
 * @param formData 表单数据
 * @returns 新增结果
 */
export const addElder = async (formData: ElderFormData): Promise<BaseResponse> => {
  const elderData = {
    name: formData.name,
    age: formData.age,
    gender: formData.gender,
    address: formData.address,
    health_notes: formData.health_notes
  };

  return request({
    url: '/api/elder/add',
    method: 'POST',
    data: {
      ...elderData,
      relativeIds: formData.relativeIds
    }
  });
};

/**
 * 编辑老人信息
 * @param formData 表单数据
 * @returns 编辑结果
 */
export const updateElder = async (formData: ElderFormData): Promise<BaseResponse> => {
  const elderData = {
    elder_id: formData.elder_id,
    name: formData.name,
    age: formData.age,
    gender: formData.gender,
    address: formData.address,
    health_notes: formData.health_notes
  };

  return request({
    url: '/api/elder/update',
    method: 'PUT',
    data: {
      ...elderData,
      relativeIds: formData.relativeIds
    }
  });
};

/**
 * 删除老人信息
 * @param elderId 老人ID
 * @returns 删除结果
 */
export const deleteElder = async (elderId: number): Promise<BaseResponse> => {
  return request({
    url: `/api/elder/delete/${elderId}`,
    method: 'DELETE'
  });
};

/**
 * 获取老人详情
 * @param elderId 老人ID
 * @returns 老人详情
 */
export const getElderDetail = async (elderId: number): Promise<BaseResponse & { data: ElderDetailItem }> => {
  return request({
    url: `/api/elder/detail/${elderId}`,
    method: 'GET'
  });
};

/**
 * 获取老人关联的家属列表
 * @param elderId 老人ID
 * @returns 关联家属列表
 */
export const getElderRelations = async (elderId: number): Promise<BaseResponse & { data: RelationItem[] }> => {
  return request({
    url: `/api/relation/list/${elderId}`,
    method: 'GET'
  });
};