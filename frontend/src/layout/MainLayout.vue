<template>
  <el-container style="height: 100vh; background-color: #ffffff;">
    <!-- 左侧导航栏（白色背景） -->
    <el-aside width="200px" style="background-color: #ffffff; color: #333333; border-right: 1px solid #e6e6e6;">
      <el-menu
          default-active="/admin/dashboard"
          router
          background-color="#ffffff"
          text-color="#333333"
          active-text-color="#409eff"
          style="height: 100%; border-right: none;"
      >
        <el-menu-item index="/admin/dashboard" style="height: 60px; line-height: 60px;">
          <span>综合数据</span>
        </el-menu-item>
        <el-menu-item index="/admin/alerts" style="height: 60px; line-height: 60px;">
          <span>告警记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/elders" style="height: 60px; line-height: 60px;">
          <span>老人管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/cameras" style="height: 60px; line-height: 60px;">
          <span>摄像头管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/fallEvent" style="height: 60px; line-height: 60px;">
          <span>跌倒事件管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主内容区 -->
    <el-container>
      <!-- 顶部标题栏（白色主题） -->
      <el-header style="background: #ffffff; color: #333333; line-height: 60px; padding: 0 20px; border-bottom: 1px solid #e6e6e6;">
        <span style="font-size: 20px; font-weight: bold; color: #1f2937;">智护安行</span>
        <span style="float: right; font-size: 12px; color: #666666;">SmartCare 智护安行</span>
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
/* ===== 全局 ===== */
.el-container {
  background:
      radial-gradient(circle at 10% 10%, #ffffff 0%, transparent 30%),
      radial-gradient(circle at 90% 0%, #eef4ff 0%, transparent 30%),
      linear-gradient(135deg, #eef3f8 0%, #f8fbff 100%);
}

/* ===== 左侧导航（软浮雕面板）===== */
.el-aside {
  margin: 16px;
  border-radius: 24px;
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  box-shadow:
      14px 14px 30px rgba(163, 177, 198, 0.28),
      -14px -14px 30px rgba(255, 255, 255, 0.95);
  border: none !important;
}

/* 菜单 */
:deep(.el-menu) {
  border: none !important;
  background: transparent;
}

/* 菜单项 */
:deep(.el-menu-item) {
  height: 52px;
  margin: 8px 12px;
  border-radius: 14px;
  font-weight: 500;
  transition: all 0.18s ease;

  background: transparent;
}

/* hover 浮起 */
:deep(.el-menu-item:hover) {
  transform: translateY(-2px);
  background: #f3f7fb;
  box-shadow:
      4px 4px 10px rgba(163, 177, 198, 0.25),
      -4px -4px 10px rgba(255, 255, 255, 0.9);
}

/* 激活态（Soft UI胶囊） */
:deep(.el-menu-item.is-active) {
  color: #fff !important;
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.3),
      -6px -6px 14px rgba(255, 255, 255, 0.9);
}

/* ===== Header（浮层）===== */
.el-header {
  margin: 16px 16px 0 0;
  border-radius: 20px;
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  box-shadow:
      10px 10px 24px rgba(163, 177, 198, 0.25),
      -10px -10px 24px rgba(255, 255, 255, 0.9);
  border: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 标题 */
.el-header span:first-child {
  font-size: 20px;
  font-weight: 700;
  color: #344054;
}

/* 右侧副标题 */
.el-header span:last-child {
  font-size: 12px;
  color: #98a2b3;
}

/* ===== 主内容区 ===== */
.el-main {
  margin: 16px;
  border-radius: 24px;
  padding: 20px;
  background:
      linear-gradient(145deg, #ffffff, #edf3f8);
  box-shadow:
      inset 6px 6px 14px rgba(163, 177, 198, 0.18),
      inset -6px -6px 14px rgba(255, 255, 255, 0.9);
}

/* ===== 子页面适配（统一风格）===== */
:deep(.elder-management-container),
:deep(.cameras-container),
:deep(.fall-event-page) {
  background: transparent !important;
  padding: 0 !important;
}

/* ===== 全局过渡 ===== */
* {
  transition: all 0.18s ease;
}
</style>