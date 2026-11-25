<template>
  <div class="page-container">
    <PageHeader title="字段映射" description="按 tagMapping 对齐渠道入参与目标入参，识别未映射字段" />
    <div class="page-card">
      <div class="search-bar">
        <span>交易码：</span>
        <el-select v-model="tranNo" filterable style="width: 220px" @change="load">
          <el-option v-for="t in transactions" :key="t.tranNo" :label="t.tranNo" :value="t.tranNo" />
        </el-select>
      </div>
      <el-table :data="rows" border size="small" v-loading="loading">
        <el-table-column prop="mapping" label="映射路径" min-width="240" />
        <el-table-column label="渠道入参" min-width="200">
          <template #default="{ row }">
            <span v-if="row.customer">{{ row.customer.tagName }} <el-tag size="small" type="info">{{ row.customer.tagType }}</el-tag></span>
            <el-tag v-else size="small" type="danger" effect="plain">缺失</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="目标入参" min-width="200">
          <template #default="{ row }">
            <span v-if="row.provider">{{ row.provider.tagName }} <el-tag size="small" type="info">{{ row.provider.tagType }}</el-tag></span>
            <el-tag v-else size="small" type="danger" effect="plain">缺失</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="160">
          <template #default="{ row }">
            <el-tag :type="row.status === 'MAPPED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'MAPPED' ? '已映射' : (row.status === 'PROVIDER_MISSING' ? '目标缺失' : '渠道缺失') }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { customerTranApi, mappingApi } from '@/api'

const transactions = ref<any[]>([])
const tranNo = ref('')
const rows = ref<any[]>([])
const loading = ref(false)

async function load() {
  if (!tranNo.value) return
  loading.value = true
  try { rows.value = (await mappingApi.compare(tranNo.value)).rows || [] } finally { loading.value = false }
}

onMounted(async () => {
  transactions.value = (await customerTranApi.list({ size: 10000 })).records || []
  if (transactions.value.length) { tranNo.value = transactions.value[0].tranNo; load() }
})
</script>
