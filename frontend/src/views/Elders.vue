<template>
  <div class="elders">
    <h2>老人管理</h2>
    <el-table :data="elderList" border>
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="age" label="年龄" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
interface Elder {
  name: string;
  age: number;
}
const elderList: Elder[] = [];
</script>

<style scoped>
.elders {
  padding: 20px;
}
</style>