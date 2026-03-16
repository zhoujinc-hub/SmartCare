<template>
  <el-container style="height: 100vh; background-color: #0a1a3a;">
    <!-- 左侧导航栏 -->
    <el-aside width="200px" style="background-color: #1f2937; color: #fff;">
      <el-menu
        default-active="/dashboard"
        router
        background-color="#1f2937"
        text-color="#fff"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <span>综合数据</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <span>告警记录</span>
        </el-menu-item>
        <el-menu-item index="/elders">
          <span>老人管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主内容区 -->
    <el-container>
      <!-- 顶部标题栏 -->
      <el-header style="background: linear-gradient(90deg, #0a1a3a, #1a3b7a); color: #fff; line-height: 60px; padding: 0 20px;">
        <span style="font-size: 20px; font-weight: bold;">智慧养老平台</span>
        <span style="float: right; font-size: 12px;">SmartCare 智慧养老监护系统</span>
      </el-header>

      <!-- 主内容区域（深色背景，用来放数据面板） -->
      <el-main style="background-color: #0a1a3a; padding: 20px;">
        <!-- 子页面渲染出口（Dashboard/告警/老人管理 在这里展示） -->
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
// 布局组件逻辑（暂时为空）
</script>

<style scoped>
/* 全局重置，避免默认边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.el-header {
  border-bottom: 1px solid #2a4a8a;
}

.el-aside {
  border-right: 1px solid #2a3a5a;
}
</style>