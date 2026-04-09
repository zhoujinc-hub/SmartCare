/**
 * 格式化百分比（将0-1的小数转为百分比字符串）
 * @param value 0-1的小数
 * @returns 百分比字符串（如 95%）
 */
export const formatPercent = (value: number): string => {
  return `${(value * 100).toFixed(0)}%`;
};

import dayjs from 'dayjs';

/**
 * 格式化发送状态文本
 * @param status 发送状态（0:失败 1:成功 2:发送中）
 */
export function formatSendStatus(status: 0 | 1 | 2): string {
  const map = {
    0: '失败',
    1: '成功',
    2: '发送中'
  };
  return map[status] || '未知';
}

/**
 * 格式化发送方式文本
 * @param method 发送方式（1:短信 2:APP推送 3:电话）
 */
export function formatSendMethod(method: 1 | 2 | 3): string {
  const map = {
    1: '短信',
    2: 'APP推送',
    3: '电话'
  };
  return map[method] || '未知';
}

/**
 * 格式化接收人类型文本
 * @param type 接收人类型（1:家属 2:管理员）
 */
export function formatRecipientType(type: 1 | 2): string {
  const map = {
    1: '家属',
    2: '管理员'
  };
  return map[type] || '未知';
}

/**
 * 格式化摔倒事件状态
 * @param status 状态（1:待处理 2:已处理 3:误报）
 */
export function formatFallStatus(status: 1 | 2 | 3): string {
  const map = {
    1: '待处理',
    2: '已处理',
    3: '误报'
  };
  return map[status] || '未知';
}

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