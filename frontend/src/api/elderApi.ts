import request from '@/utils/request';
import type { Elder, ElderFallEvent, PageQuery } from '@/types/elderType';

// ============= 家属：我的老人 =============
export function getMyElderList() {
    return request({
        url: '/family/elder/list',
        method: 'get'
    });
}

// 添加老人
export function addElder(data: Elder) {
    return request({
        url: '/family/elder/add',
        method: 'post',
        data
    });
}

// ============= 家属：老人摔倒事件 =============
export function getElderFallEvents(elderId: number, params: PageQuery) {
    return request({
        url: `/family/elder/${elderId}/fallEvents`,
        method: 'get',
        params
    });
}