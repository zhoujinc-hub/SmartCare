import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { AlertLog, AlertQueryParams } from '@/types/alertType';
import { getAlertList } from '@/api/alertApi';

export const useAlertStore = defineStore('alert', () => {
    const alertList = ref<AlertLog[]>([]);
    const pagination = ref({ pageNum: 1, pageSize: 10, total: 0 });
    const filterParams = ref<Partial<AlertQueryParams>>({});
    const loading = ref(false);

    const queryParams = computed(() => ({
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize,
        ...filterParams.value,
    }));

    const fetchAlertList = async (resetPage = false) => {
        if (resetPage) pagination.value.pageNum = 1;
        loading.value = true;
        try {
            console.log('✅ 发送到后端的参数：', queryParams.value);
            const res = await getAlertList(queryParams.value);
            alertList.value = res.data?.records || [];
            pagination.value.total = res.data?.total || 0;
        } catch (e) {
            console.error('获取告警失败', e);
        } finally {
            loading.value = false;
        }
    };

    const setFilterParams = (params: Partial<AlertQueryParams>) => {
        // ✅ 正确合并：覆盖旧参数
        filterParams.value = { ...filterParams.value, ...params };
        fetchAlertList(true);
    };

    const resetFilterParams = () => {
        filterParams.value = {};
        fetchAlertList(true);
    };

    const changePage = (newPage: number) => {
        pagination.value.pageNum = newPage;
        fetchAlertList(false);
    };

    const changeSize = (newSize: number) => {
        pagination.value.pageSize = newSize;
        fetchAlertList(true);
    };

    return {
        alertList,
        pagination,
        filterParams,
        loading,
        fetchAlertList,
        setFilterParams,
        resetFilterParams,
        changePage,
        changeSize,
    };
});