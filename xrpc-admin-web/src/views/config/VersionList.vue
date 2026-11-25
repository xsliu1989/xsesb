<template>
  <div class="page-container">
    <PageHeader title="发布管理" description="草稿 → 校验 → 审批 → 发布 → 回滚；方案 A 下发布后需重启 ESB 生效" />
    <div class="page-card">
      <el-table :data="rows" v-loading="loading" border size="small">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="targetType" label="对象类型" width="170" />
        <el-table-column prop="targetKey" label="对象键" width="140" />
        <el-table-column prop="versionNo" label="版本" width="80" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="draftBy" label="创建人" width="110" />
        <el-table-column prop="publishTime" label="发布时间" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="diff(row)">差异</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="primary" size="small" @click="approve(row)">审批通过</el-button>
            <el-button v-if="row.status === 'APPROVED'" link type="primary" size="small" @click="publish(row)">发布</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" link type="warning" size="small" @click="rollback(row)">回滚</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 14px">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
          layout="total, prev, pager, next" @current-change="load" />
      </div>
    </div>

    <el-dialog v-model="diffVisible" title="版本差异" width="700px">
      <pre class="code-block">{{ diffText }}</pre>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { versionApi } from '@/api'

const rows = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10 })
const diffVisible = ref(false)
const diffText = ref('')

async function load() {
  loading.value = true
  try { const res: any = await versionApi.page({ ...query }); rows.value = res.records; total.value = res.total }
  finally { loading.value = false }
}

function statusType(s: string) {
  return { DRAFT: 'info', APPROVED: 'warning', PUBLISHED: 'success', ROLLED_BACK: 'danger' }[s] || 'info'
}

async function diff(row: any) {
  const res: any = await versionApi.diff(row.id)
  diffText.value = JSON.stringify(res, null, 2)
  diffVisible.value = true
}

async function approve(row: any) {
  await versionApi.approve(row.id)
  ElMessage.success('已审批通过'); load()
}

async function publish(row: any) {
  await ElMessageBox.confirm('发布将写入业务元数据表，确认继续？', '提示', { type: 'warning' })
  const res: any = await versionApi.publish(row.id)
  ElMessage.warning(res?.effectMode === 'RESTART_REQUIRED' ? '发布成功，请重启 ESB 进程使配置生效' : '发布成功')
  load()
}

async function rollback(row: any) {
  await ElMessageBox.confirm('确认回滚到上一已发布版本？', '提示', { type: 'warning' })
  await versionApi.rollback(row.id)
  ElMessage.success('已回滚并生成新版本'); load()
}

onMounted(load)
</script>
