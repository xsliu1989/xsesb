<template>
  <CrudPage
    title="渠道系统"
    description="维护请求发起方系统（对应 esb_customer_sysname）"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
    status-key="flag"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { channelsApi as api } from '@/api'
const columns = [
  { prop: 'sysname', label: '渠道标识', width: 140 },
  { prop: 'systype', label: '类型', width: 120 },
  { prop: 'str1', label: '渠道名称' },
  { prop: 'size', label: '并发上限', width: 110 },
  { prop: 'flag', label: '状态', width: 100, type: 'status' }
]
const searchFields = [{ prop: 'sysname', label: '渠道标识' }, { prop: 'str1', label: '渠道名称' }]
const fields = [
  { prop: 'sysname', label: '渠道标识', placeholder: 'CH01' },
  { prop: 'systype', label: '类型', default: 'CHANNEL' },
  { prop: 'str1', label: '渠道名称' },
  { prop: 'size', label: '并发上限', type: 'number', default: 100 },
  { prop: 'flag', label: '状态', options: [{ value: 'true', label: '启用' }, { value: 'false', label: '停用' }], default: 'true' },
  { prop: 'str2', label: '扩展字段 2' }, { prop: 'str3', label: '扩展字段 3' },
  { prop: 'str4', label: '扩展字段 4' }, { prop: 'str5', label: '扩展字段 5' }
]
</script>
