<template>
  <div class="page-container">
    <PageHeader title="操作审计" description="谁在什么时候改了什么；记录不可删除" />
    <div class="page-card">
      <el-table :data="rows" border size="small" v-loading="loading">
        <el-table-column prop="operateTime" label="时间" width="180" />
        <el-table-column prop="operator" label="操作人" width="110" />
        <el-table-column prop="module" label="模块" width="140" />
        <el-table-column prop="action" label="动作" width="90" />
        <el-table-column prop="target" label="对象" width="120" />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column label="详情" width="90">
          <template #default="{ row }"><el-button link type="primary" size="small" @click="open(row)">查看</el-button></template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 14px">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
          layout="total, prev, pager, next" @current-change="load" />
      </div>
    </div>
    <el-dialog v-model="visible" title="变更详情" width="700px">
      <h4>变更前</h4><pre class="code-block">{{ current?.beforeJson || '（无）' }}</pre>
      <h4>变更后</h4><pre class="code-block">{{ current?.afterJson || '（无）' }}</pre>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { auditApi } from '@/api'

const rows = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10 })
const visible = ref(false)
const current = ref<any>(null)

async function load() {
  loading.value = true
  try { const res: any = await auditApi.page({ ...query }); rows.value = res.records; total.value = res.total }
  finally { loading.value = false }
}
function open(row: any) { current.value = row; visible.value = true }

onMounted(load)
</script>
