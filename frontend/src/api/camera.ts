import request from '../utils/request';
import type { 
  CameraItem, 
  CameraFormData, 
  HouseholdItem,
  PageResponse,
  BaseResponse
} from '../types/camera';

/**
 * 获取摄像头列表（分页+筛选）
 */
export const getCameraList = async (
  params: {
    cameraName: string;
    status: number | '';
    householdId: number | '';
    pageNum: number;
    pageSize: number;
  }
): Promise<PageResponse<CameraItem>> => {
  return request({
    url: '/api/camera/list',
    method: 'GET',
    params
  });
};

/**
 * 获取家庭列表（用于关联选择）
 */
export const getHouseholdList = async (): Promise<BaseResponse & { data: HouseholdItem[] }> => {
  return request({
    url: '/api/household/list',
    method: 'GET'
  });
};

/**
 * 新增摄像头
 */
export const addCamera = async (formData: CameraFormData): Promise<BaseResponse> => {
  const cameraData = {
    camera_type: formData.camera_type,
    camera_name: formData.camera_name,
    device_serial: formData.device_serial,
    stream_url: formData.stream_url,
    location_desc: formData.location_desc,
    latitude: formData.latitude,
    longitude: formData.longitude,
    status: formData.status
  };
  return request({
    url: '/api/camera/add',
    method: 'POST',
    data: {
      ...cameraData,
      householdId: formData.householdId,
      remark: formData.remark
    }
  });
};

/**
 * 编辑摄像头
 */
export const updateCamera = async (formData: CameraFormData): Promise<BaseResponse> => {
  const cameraData = {
    camera_id: formData.camera_id,
    camera_type: formData.camera_type,
    camera_name: formData.camera_name,
    device_serial: formData.device_serial,
    stream_url: formData.stream_url,
    location_desc: formData.location_desc,
    latitude: formData.latitude,
    longitude: formData.longitude,
    status: formData.status
  };
  return request({
    url: '/api/camera/update',
    method: 'PUT',
    data: {
      ...cameraData,
      householdId: formData.householdId,
      remark: formData.remark
    }
  });
};

/**
 * 删除摄像头
 */
export const deleteCamera = async (cameraId: number): Promise<BaseResponse> => {
  return request({
    url: `/api/camera/delete/${cameraId}`,
    method: 'DELETE'
  });
};

/**
 * 获取摄像头详情
 */
export const getCameraDetail = async (cameraId: number): Promise<BaseResponse & { data: CameraItem }> => {
  return request({
    url: `/api/camera/detail/${cameraId}`,
    method: 'GET'
  });
};

/**
 * 刷新摄像头状态
 */
export const refreshCameraStatus = async (): Promise<BaseResponse> => {
  return request({
    url: '/api/camera/refreshStatus',
    method: 'POST'
  });
};