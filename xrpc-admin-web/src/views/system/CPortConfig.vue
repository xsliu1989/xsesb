<template>
  <CrudPage
    title="TCP 端口映射"
    description="可视化维护原 cPort.json（地址 / 端口 / 字符集 / 长度位宽）"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { cportApi as api } from '@/api'
const columns = [
  { prop: 'trannos', label: '交易码列表', width: 200 },
  { prop: 'address', label: '地址', width: 140 },
  { prop: 'port', label: '端口', width: 90 },
  { prop: 'charset', label: '字符集', width: 100 },
  { prop: 'fixedlength', label: '长度位宽', width: 100 },
  { prop: 'type', label: '协议', width: 90 }
]
const searchFields = [{ prop: 'trannos', label: '交易码' }]
const fields = [
  { prop: 'trannos', label: '交易码列表', placeholder: '多个用逗号分隔' },
  { prop: 'address', label: '地址' },
  { prop: 'port', label: '端口', type: 'number', default: 8888 },
  { prop: 'charset', label: '字符集', default: 'UTF-8' },
  { prop: 'fixedlength', label: '长度位宽', type: 'number', default: 6 },
  { prop: 'type', label: '协议', options: [{ value: 'DC', label: 'DC' }, { value: 'JSON', label: 'JSON' }, { value: 'XML', label: 'XML' }], default: 'DC' }
]
</script>
