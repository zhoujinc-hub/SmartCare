import request from '@/utils/request';
import type { AlertQueryParams,HandleAlertParams } from '@/types/alertType';

// 获取告警列表
export function getAlertList(params: AlertQueryParams) {
  return request({
    url: '/alertLogs/list',
    method: 'get',
    params
  });
}

// 获取告警详情
export function getAlertDetail(alertId: bigint) {
  return request({
    url: `/alertLogs/${alertId}`,
    method: 'get'
  });
}

// 处理告警
export function handleAlert(params: HandleAlertParams) {
  return request({
    url: '/alertLogs/handle',
    method: 'put',
    data: params
  });
}

// 重发告警
export function resendAlert(alertId: bigint) {
  return request({
    url: `/alertLogs/${alertId}/resend`,
    method: 'post'
  });
}