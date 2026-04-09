import request from '@/utils/request';
import type { AlertQueryParams, AlertPageResult, AlertLog, HandleAlertParams } from '@/types/alertType';

// 获取告警列表
export function getAlertList(params: AlertQueryParams) {
  return request({
    url: '/alert-logs',
    method: 'get',
    params
  });
}

// 获取告警详情
export function getAlertDetail(alertId: bigint) {
  return request({
    url: `/alert-logs/${alertId}`,
    method: 'get'
  });
}

// 处理告警
export function handleAlert(params: HandleAlertParams) {
  return request({
    url: '/alert-logs/handle',
    method: 'put',
    data: params
  });
}

// 重发告警
export function resendAlert(alertId: bigint) {
  return request({
    url: `/alert-logs/${alertId}/resend`,
    method: 'post'
  });
}