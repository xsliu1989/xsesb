<template>
  <div class="page-container">
    <PageHeader title="Tag 配置" description="以树形结构配置源数据 / 目标数据的入参与出参">
      <template #actions>
        <el-button type="primary" @click="saveDraft">保存为草稿</el-button>
        <el-button @click="goVersions">前往发布管理</el-button>
      </template>
    </PageHeader>

    <div class="page-card">
      <div class="search-bar">
        <span>交易码：</span>
        <el-select v-model="tranNo" filterable style="width: 220px" @change="load">
          <el-option v-for="t in transactions" :key="t.tranNo" :label="t.tranNo" :value="t.tranNo" />
        </el-select>
        <span style="margin-left: 12px">报文类型：</span>
        <el-tag :type="protocol === 'dc' ? 'warning' : 'primary'" size="small">{{ protocol }}</el-tag>
      </div>

      <el-tabs v-model="ownerType" @tab-change="load">
        <el-tab-pane v-for="t in ownerTypes" :key="t.value" :label="t.label" :name="t.value" />
      </el-tabs>

      <TagTreeEditor
        v-if="tranNo"
        ref="editorRef"
        v-model="tags"
        :owner-key="ownerKey"
        :protocol="protocol"
        @validate="onValidate"
      />
      <el-empty v-else description="请先选择一个交易码" />
    </div>

    <div class="page-card" v-if="validation">
      <div class="panel-title">校验与预览</div>
      <el-alert v-for="(e, i) in validation.errors" :key="i" :title="e.message" type="error" :closable="false" show-icon style="margin-bottom: 6px" />
      <el-alert v-if="!validation.errors?.length" title="校验通过" type="success" :closable="false" show-icon style="margin-bottom: 10px" />
      <el-table :data="validation.preview || []" size="small" border>
        <el-table-column prop="tagId" label="Tag ID" width="90" />
        <el-table-column prop="path" label="映射路径" />
        <el-table-column prop="value" label="样例取值" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import TagTreeEditor from '@/components/TagTreeEditor.vue'
import { customerTranApi, providerTranApi, tagApi, versionApi } from '@/api'
import type { TagNode } from '@/utils/tagTree'

const router = useRouter()
const ownerTypes = [
  { value: 'CUSTOMER_REQUEST', label: '源数据入参（渠道请求）' },
  { value: 'CUSTOMER_RESPONSE', label: '源数据出参（渠道返回）' },
  { value: 'PROVIDER_REQUEST', label: '目标数据入参（数据源请求）' },
  { value: 'PROVIDER_RESPONSE', label: '目标数据出参（数据源返回）' }
]
const ownerType = ref('CUSTOMER_REQUEST')
const tranNo = ref('')
const transactions = ref<any[]>([])
const providerTransactions = ref<any[]>([])
const tags = ref<TagNode[]>([])
const validation = ref<any>(null)
const editorRef = ref<any>(null)
const protocol = ref('json')

// 后台：渠道 Tag 按 tranno 存储，数据源 Tag 按 providertranno 存储
const ownerKey = computed(() => {
  if (!ownerType.value.startsWith('PROVIDER')) return tranNo.value
  const p = providerTransactions.value.find((x: any) => x.customerTranNo === tranNo.value)
  return p?.providerTranNo || tranNo.value
})

async function load() {
  if (!tranNo.value) return
  const p = providerTransactions.value.find((x: any) => x.customerTranNo === tranNo.value)
  protocol.value = p?.dataType || 'json'
  tags.value = await tagApi.list({ ownerType: ownerType.value, ownerKey: ownerKey.value })
  validation.value = null
}

async function onValidate(payload: any) {
  const sample = ownerType.value.includes('RESPONSE')
    ? '{"response":{"returnCode":"000000","returnMess":"成功","retCd":"000000","retMsg":"成功"}}'
    : '{"transaction":{"header":{"msg":{"seqNb":"111106500000497","sndDt":"20220113"}},"body":{"request":{"orderId":"O123"}}}}'
  validation.value = await tagApi.validate({
    ownerType: ownerType.value, ownerKey: ownerKey.value, protocol: protocol.value,
    sample, tags: payload.tags
  })
}

async function saveDraft() {
  const current = editorRef.value?.tags() || tags.value
  if (!tranNo.value || !current.length) { ElMessage.warning('请选择交易码并配置 Tag'); return }
  await versionApi.draft({
    env: 'dev', targetType: ownerType.value, targetKey: ownerKey.value,
    content: current, remark: `${ownerType.value} 草稿`
  })
  tags.value = current
  ElMessage.success('草稿已保存，请在发布管理中提交审批')
}

function goVersions() { router.push('/config/versions') }

onMounted(async () => {
  transactions.value = (await customerTranApi.list({ size: 10000 })).records || []
  providerTransactions.value = (await providerTranApi.list({ size: 10000 })).records || []
  if (transactions.value.length) { tranNo.value = transactions.value[0].tranNo; load() }
})
</script>

<style scoped>
.panel-title { font-weight: 600; font-size: 13px; margin-bottom: 10px; }
</style>
