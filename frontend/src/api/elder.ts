import request from '../utils/request';
import type {
  ElderItem,
  ElderFormData,
  ElderDetailItem,
} from '../types/elder';

import type {
  PageResponse,
  BaseResponse,
  RelationItem
} from '../types/user';

interface PaginationParams {
  pageNum: number;
  pageSize: number;
}

/**
 * 获取老人列表（分页+筛选）
 */
export const getElderList = async (
  pagination: PaginationParams,
  filterParams: { name?: string }
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
 * 新增老人
 */
export const addElder = async (formData: ElderFormData): Promise<BaseResponse> => {
  return request({
    url: '/api/elder/add',
    method: 'POST',
    data: formData
  });
};

/**
 * 修改老人
 */
export const updateElder = async (formData: ElderFormData): Promise<BaseResponse> => {
  return request({
    url: '/api/elder/update',
    method: 'PUT',
    data: formData
  });
};

/**
 * 删除老人
 */
export const deleteElder = async (elderId: number): Promise<BaseResponse> => {
  return request({
    url: `/api/elder/delete/${elderId}`,
    method: 'DELETE'
  });
};

/**
 * 获取老人详情
 */
export const getElderDetail = async (elderId: number): Promise<BaseResponse & { data: ElderDetailItem }> => {
  return request({
    url: `/api/elder/detail/${elderId}`,
    method: 'GET'
  });
};

/**
 * 获取老人关联家属
 */
export const getElderRelations = async (elderId: number): Promise<BaseResponse & { data: RelationItem[] }> => {
  return request({
    url: `/api/relation/list/${elderId}`,
    method: 'GET'
  });
};