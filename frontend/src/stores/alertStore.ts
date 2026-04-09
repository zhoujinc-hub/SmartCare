import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { AlertLog, AlertQueryParams } from '@/types/alertType';
import { getAlertList } from '@/api/alertApi';

export const useAlertStore = defineStore('alert', () => {
  // 状态：告警列表
  const alertList = ref<AlertLog[]>([]);
  // 状态：分页信息
  const pagination = ref({
    page: 1,
    size: 10,
    total: 0
  });
  // 状态：筛选条件
  const filterParams = ref<Omit<AlertQueryParams, 'page' | 'size'>>({});
  // 状态：加载中
  const loading = ref(false);
  // 状态：详情弹窗显隐
  const detailDialogVisible = ref(false);
  // 状态：当前选中的告警详情
  const currentAlert = ref<AlertLog | null>(null);

  // 计算属性：完整的查询参数（分页+筛选）
  const queryParams = computed(() => ({
    page: pagination.value.page,
    size: pagination.value.size,
    ...filterParams.value
  }));

  /**
   * 获取告警列表
   * @param resetPage 是否重置页码为1
   */
  const fetchAlertList = async (resetPage = true) => {
    if (resetPage) {
      pagination.value.page = 1;
    }
    loading.value = true;
    try {
      // ✅ 修复：axios 响应要取 .data
      const res = await getAlertList(queryParams.value);
      const data = res.data || res;

      alertList.value = data.list || [];
      pagination.value.total = data.total || 0;
      pagination.value.page = data.page || 1;
      pagination.value.size = data.size || 10;
    } catch (error) {
      console.error('获取告警列表失败：', error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 设置筛选条件
   * @param params 筛选参数
   */
  const setFilterParams = (params: Partial<AlertQueryParams>) => {
    filterParams.value = { ...filterParams.value, ...params };
    // 筛选条件变化，重新获取列表
    fetchAlertList(true);
  };

  /**
   * 重置筛选条件
   */
  const resetFilterParams = () => {
    filterParams.value = {};
    fetchAlertList(true);
  };

  /**
   * 设置当前告警详情
   * @param alert 告警信息
   */
  const setCurrentAlert = (alert: AlertLog | null) => {
    currentAlert.value = alert;
    detailDialogVisible.value = !!alert;
  };

  /**
   * 分页切换
   * @param page 页码
   */
  const changePage = (page: number) => {
    pagination.value.page = page;
    fetchAlertList(false);
  };

  /**
   * 每页条数切换
   * @param size 每页条数
   */
  const changeSize = (size: number) => {
    pagination.value.size = size;
    fetchAlertList(true);
  };

  return {
    alertList,
    pagination,
    filterParams,
    loading,
    detailDialogVisible,
    currentAlert,
    fetchAlertList,
    setFilterParams,
    resetFilterParams,
    setCurrentAlert,
    changePage,
    changeSize
  };
});