<template>
  <div class="page-container">
    <PageHeader title="缓存状态" description="查看 ESB 元数据缓存模式与条目数" />
    <div class="page-card">
      <el-alert type="warning" :closable="false" show-icon
        :title="`当前生效方式：${status?.effectMode === 'RESTART_REQUIRED' ? '方案 A（发布后需重启 ESB）' : '自动刷新'}`"
        style="margin-bottom: 16px" />
      <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
        <el-descriptions-item label="缓存模式">{{ status?.cacheName }}</el-descriptions-item>
        <el-descriptions-item label="刷新端点">{{ (status?.refreshEndpoints || []).length }} 个</el-descriptions-item>
      </el-descriptions>
      <el-table :data="status?.hashes || []" border size="small">
        <el-table-column prop="name" label="缓存 Hash" />
        <el-table-column prop="count" label="条目数" />
      </el-table>
      <div style="margin-top: 16px">
        <el-button type="primary" @click="refresh">触发刷新</el-button>
        <span class="text-muted" style="margin-left: 12px">方案 A 下仅提示，不实际刷新</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { systemApi } from '@/api'

const status = ref<any>(null)

async function refresh() {
  const res: any = await systemApi.refresh()
  ElMessage.warning(res?.message || '已触发')
}

onMounted(async () => { status.value = await systemApi.cacheStatus() })
</script>
