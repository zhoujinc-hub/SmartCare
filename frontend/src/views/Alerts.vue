<template>
  <div class="alerts">
    <h2>告警记录</h2>
    <el-table :data="alertList" border>
      <el-table-column prop="time" label="告警时间" />
      <el-table-column prop="type" label="告警类型" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
interface Alert {
  time: string;
  type: string;
}
const alertList: Alert[] = [];
</script>

<style scoped>
.alerts {
  padding: 20px;
}
</style>