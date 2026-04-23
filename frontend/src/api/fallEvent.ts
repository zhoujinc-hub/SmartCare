import request from '@/utils/request'
// 只保留导入，删除文件里重复的interface定义
import type {
    FallEventQueryParams,
    FallEventHandleDto,
    FallEventNotesDto,
    FallEventVo,
    PageVo,
    FallEventListResponse,
    FallEventDetailResponse,
    FallEventBaseResponse
} from '@/types/fallEvent'

export function getFallEventList(data: FallEventQueryParams): Promise<FallEventListResponse> {
    return request({
        url: '/fallEvents/list',
        method: 'POST',
        data
    })
}

export function getFallEventDetail(eventId: number): Promise<FallEventDetailResponse> {
    return request({
        url: `/fallEvents/detail/${eventId}`,
        method: 'GET'
    })
}

export function handleFallEvent(eventId: number, data: FallEventHandleDto): Promise<FallEventBaseResponse> {
    return request({
        url: `/fallEvents/handle/${eventId}`,
        method: 'PUT',
        data
    })
}

export function saveFallEventNotes(eventId: number, data: FallEventNotesDto): Promise<FallEventBaseResponse> {
    return request({
        url: `/fallEvents/notes/${eventId}`,
        method: 'PUT',
        data
    })
}