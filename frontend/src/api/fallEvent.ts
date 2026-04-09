import request from '../utils/request';
import type { 
  AlertItem, 
  AlertFilterParams, 
  PaginationParams, 
  AlertListResponse, 
  BaseResponse 
} from '../types/fallEvent';

/**
 * 获取告警列表（分页+筛选）
 * @param pagination 分页参数
 * @param filterParams 筛选参数
 * @returns 告警列表数据
 */
export const getAlertList = async (
  pagination: Pick<PaginationParams, 'pageNum' | 'pageSize'>,
  filterParams: AlertFilterParams
): Promise<AlertListResponse> => {
  const params = {
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    cameraType: filterParams.eventType || undefined,
    status: filterParams.status || undefined,
    startTime: filterParams.dateRange?.[0] || undefined,
    endTime: filterParams.dateRange?.[1] || undefined
  };

  return request({
    url: '/api/fall-events/list',
    method: 'POST',
    data: params
  });
};

/**
 * 标记告警为已处理
 * @param eventId 告警ID
 * @param processedBy 处理人ID
 * @returns 处理结果
 */
export const handleAlert = async (
  eventId: number,
  processedBy: number
): Promise<BaseResponse> => {
  return request({
    url: `/api/fall-events/handle/${eventId}`,
    method: 'PUT',
    data: {
      status: 2,
      processedBy,
      processedAt: new Date().toISOString()
    }
  });
};

/**
 * 保存告警处理备注
 * @param eventId 告警ID
 * @param processNotes 处理备注
 * @returns 保存结果
 */
export const saveAlertNotes = async (
  eventId: number,
  processNotes: string
): Promise<BaseResponse> => {
  return request({
    url: `/api/fall-events/notes/${eventId}`,
    method: 'PUT',
    data: { processNotes }
  });
};