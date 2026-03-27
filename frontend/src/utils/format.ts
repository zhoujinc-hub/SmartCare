/**
 * 格式化百分比（将0-1的小数转为百分比字符串）
 * @param value 0-1的小数
 * @returns 百分比字符串（如 95%）
 */
export const formatPercent = (value: number): string => {
  return `${(value * 100).toFixed(0)}%`;
};

/**
 * 格式化告警类型文本
 * @param cameraType 摄像头类型（0:家庭 1:社区）
 * @returns 告警类型文本
 */
export const formatAlertType = (cameraType: number): string => {
  return cameraType === 0 ? '家庭摔倒' : '社区摔倒';
};

/**
 * 格式化处理状态文本
 * @param status 处理状态（1:待处理 2:已处理 3:误报）
 * @returns 状态文本
 */
export const formatHandleStatus = (status: number): string => {
  switch (status) {
    case 1: return '待处理';
    case 2: return '已处理';
    case 3: return '误报';
    default: return '未知';
  }
};

/**
 * 格式化通知状态文本
 * @param alertSent 是否通知成功
 * @returns 状态文本
 */
export const formatAlertSentStatus = (alertSent: boolean): string => {
  return alertSent ? '已通知' : '未通知';
};

/**
 * 格式化是否注册文本
 * @param isRegistered 是否注册（0:未 1:已）
 * @returns 文本
 */
export const formatIsRegistered = (isRegistered: number): string => {
  return isRegistered === 1 ? '已注册' : '未注册';
};

import type { UserItem } from '@/types/user';

/**
 * 格式化时间（数据库timestamp转本地格式）
 * @param timeStr 时间字符串
 * @returns 格式化后的本地时间字符串
 */
export const formatTime = (timeStr: string): string => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

/**
 * 格式化性别文本
 * @param gender 性别值（0:女 1:男）
 * @returns 性别文本
 */
export const formatGender = (gender: number): string => {
  return gender === 1 ? '男' : '女';
};

/**
 * 拼接家属信息文本
 * @param relativeList 家属列表
 * @returns 拼接后的文本
 */
export const formatRelativesText = (relativeList?: UserItem[]): string => {
  return relativeList?.map(r => `${r.real_name}(${r.phone})`).join('，') || '暂无';
};