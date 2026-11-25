<template>
  <div class="page-container">
    <PageHeader title="交易日志" description="按交易码、渠道、流水号查询完整链路" />
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.tranflag" placeholder="serid / 链路号" clearable style="width: 220px" />
        <el-input v-model="query.trancode" placeholder="交易码" clearable style="width: 160px" />
        <el-input v-model="query.reqchannel" placeholder="渠道" clearable style="width: 140px" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table :data="rows" border size="small" v-loading="loading" @row-click="openDetail">
        <el-table-column prop="tranflag" label="serid" width="180" show-overflow-tooltip />
        <el-table-column prop="nonce" label="流水号" width="200" show-overflow-tooltip />
        <el-table-column prop="trancode" label="交易码" width="130" />
        <el-table-column prop="reqchannel" label="渠道" width="100" />
        <el-table-column prop="startdate" label="开始时间" width="180" />
        <el-table-column prop="res" label="响应码" width="100" />
        <el-table-column prop="logflag" label="结果" width="90">
          <template #default="{ row }"><el-tag :type="row.logflag === 'OK' ? 'success' : 'danger'" size="small">{{ row.logflag }}</el-tag></template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 14px">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
          layout="total, prev, pager, next" @current-change="load" />
      </div>
    </div>

    <el-drawer v-model="detailVisible" title="交易详情" size="60%">
      <template v-if="detail">
        <h4>四段式转换快照</h4>
        <div v-for="s in detail.stages || []" :key="s.name" style="margin-bottom: 12px">
          <div class="panel-title">{{ s.name }}</div>
          <pre class="code-block">{{ pretty(s.content) }}</pre>
        </div>
        <h4>渠道响应</h4>
        <pre class="code-block">{{ pretty(detail.response) }}</pre>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { logApi } from '@/api'
import { pretty } from '@/utils/format'

const rows = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive<any>({ page: 1, size: 10, tranflag: '', trancode: '', reqchannel: '' })
const detailVisible = ref(false)
const detail = ref<any>(null)

async function load() {
  loading.value = true
  try { const res: any = await logApi.page({ ...query }); rows.value = res.records; total.value = res.total }
  finally { loading.value = false }
}

async function openDetail(row: any) {
  detail.value = await logApi.detail(row.tranflag)
  detailVisible.value = true
}

onMounted(load)
</script>

<style scoped>
.panel-title { font-weight: 600; font-size: 13px; margin-bottom: 6px; }
</style>
