<template>
  <el-container style="height: 100vh; background-color: #ffffff;">
    <!-- 左侧导航栏（白色背景） -->
    <el-aside width="200px" style="background-color: #ffffff; color: #333333; border-right: 1px solid #e6e6e6;">
      <el-menu
        default-active="/dashboard"
        router
        background-color="#ffffff"
        text-color="#333333"
        active-text-color="#409eff"
        style="height: 100%; border-right: none;"
      >
        <el-menu-item index="/dashboard" style="height: 60px; line-height: 60px;">
          <span>综合数据</span>
        </el-menu-item>
        <el-menu-item index="/alerts" style="height: 60px; line-height: 60px;">
          <span>告警记录</span>
        </el-menu-item>
        <el-menu-item index="/elders" style="height: 60px; line-height: 60px;">
          <span>老人管理</span>
        </el-menu-item>
        <el-menu-item index="/households" style="height: 60px; line-height: 60px;">
          <span>家庭管理</span>
        </el-menu-item>
        <el-menu-item index="/cameras" style="height: 60px; line-height: 60px;">
          <span>摄像头管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主内容区 -->
    <el-container>
      <!-- 顶部标题栏（白色主题） -->
      <el-header style="background: #ffffff; color: #333333; line-height: 60px; padding: 0 20px; border-bottom: 1px solid #e6e6e6;">
        <span style="font-size: 20px; font-weight: bold; color: #1f2937;">智慧养老平台</span>
        <span style="float: right; font-size: 12px; color: #666666;">SmartCare 智慧养老监护系统</span>
      </el-header>

      <!-- 主内容区域（浅灰背景，提升层次感） -->
      <el-main style="background-color: #f9f9f9; padding: 20px;">
        <!-- 子页面渲染出口 -->
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
// 布局组件逻辑
</script>

<style scoped>
/* 全局重置，避免默认边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 菜单样式优化 - 白色主题 */
:deep(.el-menu) {
  --el-menu-hover-text-color: #409eff;
  --el-menu-active-text-color: #409eff;
  --el-menu-bg-color: #ffffff;
  --el-menu-text-color: #333333;
  --el-menu-border-color: #e6e6e6;
}

:deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  --el-menu-item-hover-bg-color: #f0f9ff;
  --el-menu-item-active-bg-color: #e6f7ff;
  border-radius: 0;
}

:deep(.el-menu-item:hover),
:deep(.el-menu-item.is-active) {
  background-color: #f0f9ff !important;
  color: #409eff !important;
}

/* 子页面容器样式适配（白色主题） */
:deep(.elders-container) {
  background-color: #f9f9f9 !important;
  color: #333333 !important;
}

/* Header样式 */
.el-header {
  border-bottom: 1px solid #e6e6e6;
}

/* Aside样式 */
.el-aside {
  border-right: 1px solid #e6e6e6;
}
</style>