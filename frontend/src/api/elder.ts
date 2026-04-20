import request from '../utils/request';
import type { ElderItem, ElderFormData } from '../types/elder';
import type { PageResponse, BaseResponse } from '../types/user';

interface PaginationParams {
    pageNum: number;
    pageSize: number;
}

// 列表
export const getElderList = (params: {
    name?: string
    pageNum: number
    pageSize: number
}) => {
    return request({
        url: '/elders/list',
        method: 'POST', // 必须是 POST！
        data: params    // 所有参数放在 data 里
    })
}

// 新增
export const addElder = (data: ElderFormData): Promise<BaseResponse> => {
    return request({
        url: '/elders/add',
        method: 'POST',
        data
    });
};

// 修改
export const updateElder = (data: ElderItem): Promise<BaseResponse> => {
    return request({
        url: '/elders/update',
        method: 'PUT',
        data
    });
};

// 删除
export const deleteElder = (elderId: number): Promise<BaseResponse> => {
    return request({
        url: `/elders/delete/${elderId}`,
        method: 'DELETE'
    });
};

// 详情
export const getElderDetail = (elderId: number): Promise<{ data: ElderItem }> => {
    return request({
        url: `/elders/detail/${elderId}`,
        method: 'GET'
    });
};