<template>

  <el-container style="height:100vh">

    <el-aside width="200px">

      <el-menu router>

        <el-menu-item index="/">
          Dashboard
        </el-menu-item>

        <el-menu-item index="/alerts">
          告警记录
        </el-menu-item>

        <el-menu-item index="/elders">
          老人管理
        </el-menu-item>

      </el-menu>

    </el-aside>

    <el-container>

      <el-header>
        SmartCare 智慧养老监护系统
      </el-header>

      <el-main>
        <router-view />
      </el-main>

    </el-container>

  </el-container>

</template>