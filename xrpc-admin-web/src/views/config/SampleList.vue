<template>
  <CrudPage
    title="报文样例"
    description="黄金样例，可作为 Tag 骨架推断与校验输入"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { sampleApi as api } from '@/api'
const columns = [
  { prop: 'tranNo', label: '交易码', width: 140 },
  { prop: 'sampleType', label: '类型', width: 110 },
  { prop: 'remark', label: '说明' },
  { prop: 'content', label: '报文内容' }
]
const searchFields = [{ prop: 'tranNo', label: '交易码' }]
const fields = [
  { prop: 'tranNo', label: '交易码' },
  { prop: 'sampleType', label: '类型', options: [{ value: 'REQUEST', label: '请求' }, { value: 'RESPONSE', label: '响应' }], default: 'REQUEST' },
  { prop: 'content', label: '报文内容' },
  { prop: 'remark', label: '说明' }
]
</script>
