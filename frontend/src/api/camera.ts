import request from '@/utils/request';
import type {
    CameraQueryParams,
    CameraPageResponse,
    CameraDetailResponse,
    CameraAddDto,
    CameraUpdateDto,
    CameraBaseResponse
} from '@/types/camera';

// 获取摄像头分页列表
export function getCameraList(params: CameraQueryParams) {
    return request({
        url: '/cameras/list',
        method: 'get',
        params
    });
}

// 获取摄像头详情
export function getCameraDetail(cameraId: number) {
    return request({
        url: `/cameras/detail/${cameraId}`,
        method: 'get'
    });
}

// 新增摄像头
export function addCamera(data: CameraAddDto) {
    return request({
        url: '/cameras/add',
        method: 'post',
        data
    });
}

// 修改摄像头
export function updateCamera(data: CameraUpdateDto) {
    return request({
        url: '/cameras/update',
        method: 'put',
        data
    });
}

// 删除摄像头
export function deleteCamera(cameraId: number) {
    return request({
        url: `/cameras/delete/${cameraId}`,
        method: 'delete'
    });
}

// 刷新状态
export function refreshCameraStatus() {
    return request({
        url: '/cameras/refreshStatus',
        method: 'post'
    });
}