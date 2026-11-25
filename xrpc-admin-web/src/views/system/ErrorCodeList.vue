<template>
  <CrudPage
    title="错误码字典"
    description="与 05 附录现状保持一致；新增码需唯一"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { errorCodeApi as api } from '@/api'
const columns = [
  { prop: 'code', label: '错误码', width: 120 },
  { prop: 'message', label: '消息' },
  { prop: 'module', label: '来源模块', width: 140 },
  { prop: 'suggestion', label: '处理建议' }
]
const searchFields = [{ prop: 'code', label: '错误码' }, { prop: 'message', label: '消息' }]
const fields = [
  { prop: 'code', label: '错误码' }, { prop: 'message', label: '消息' },
  { prop: 'module', label: '来源模块' }, { prop: 'suggestion', label: '处理建议' }
]
</script>
