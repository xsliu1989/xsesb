<template>
  <div class="page-container">
    <PageHeader title="日志开关" description="可视化维护原 logConfig.json 的 8 类日志开关" />
    <div class="page-card">
      <el-table :data="rows" border size="small" v-loading="loading">
        <el-table-column prop="logType" label="编号" width="90" />
        <el-table-column prop="remark" label="日志类型" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-switch v-model="row.enabled" active-value="OK" inactive-value="OFF" @change="save(row)" />
          </template>
        </el-table-column>
      </el-table>
      <el-alert type="info" :closable="false" style="margin-top: 12px"
        title="日志开关由 logConfig.json 文件监听控制；方案 A 下数据库配置不直接生效。" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { systemApi } from '@/api'

const rows = ref<any[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try { rows.value = await systemApi.logSwitches() } finally { loading.value = false }
}

async function save(row: any) {
  await systemApi.saveLogSwitch(row)
  ElMessage.success('已保存')
}

onMounted(load)
</script>
