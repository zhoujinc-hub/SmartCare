import request from '@/utils/request'

// 查询参数类型
interface FallEventQuery {
    elderName?: string
    cameraId?: number
    status?: number
    pageNum: number
    pageSize: number
}

// 处理事件参数
interface HandleEventData {
    status: number
    processNotes?: string
}

// 获取跌倒事件列表
export function getFallEventList(data: FallEventQuery) {
    return request({
        url: '/fallEvents/list',
        method: 'post',
        data
    })
}

// 获取详情
export function getFallEventDetail(eventId: number) {
    return request({
        url: `/fallEvents/detail/${eventId}`,
        method: 'get'
    })
}

// 处理事件
export function handleFallEvent(eventId: number, data: HandleEventData) {
    return request({
        url: `/fallEvents/handle/${eventId}`,
        method: 'put',
        data
    })
}

// 添加备注
export function saveFallEventNotes(eventId: number, processNotes: string) {
    return request({
        url: `/fallEvents/notes/${eventId}`,
        method: 'put',
        data: { processNotes }
    })
}