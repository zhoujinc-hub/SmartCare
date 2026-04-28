import request from '@/utils/request';
import type { Elder, ElderFallEvent, PageQuery } from '@/types/elderType';

// 获取我的老人
export function getMyElderList(userId: number) {
    return request({
        url: `/family/elder/list/${userId}`,
        method: 'get'
    });
}

// 添加老人
export function addElder(userId: number, data: Elder) {
    return request({
        url: `/family/elder/add/${userId}`,
        method: 'post',
        data
    });
}

// 删除老人
export function deleteElder(userId: number,elderId: number) {
    return request({
        url: `/family/elder/${userId}/${elderId}/delete`,
        method: 'post'
    });
}

// 获取摔倒事件
export function getElderFallEvents(userId: number, elderId: number, params: PageQuery) {
    return request({
        url: `/family/elder/${userId}/${elderId}/fallEvents`,
        method: 'get',
        params
    });
}