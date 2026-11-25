<template>
  <CrudPage
    title="渠道接口"
    description="维护渠道侧交易码、上下架与并发（对应 esb_customer_transaction_info）"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
    status-key="isOpen"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { customerTranApi as api } from '@/api'
const columns = [
  { prop: 'tranNo', label: '交易码', width: 140 },
  { prop: 'sysName', label: '允许渠道', width: 160 },
  { prop: 'clazz', label: '处理 Bean', width: 140 },
  { prop: 'isCryption', label: '加解密', width: 100, type: 'boolean' },
  { prop: 'size', label: '并发上限', width: 110 },
  { prop: 'isOpen', label: '上下架', width: 100, type: 'status' }
]
const searchFields = [{ prop: 'tranNo', label: '交易码' }, { prop: 'sysName', label: '渠道' }]
const fields = [
  { prop: 'tranNo', label: '交易码', placeholder: 'TRANNO001' },
  { prop: 'sysName', label: '允许渠道', placeholder: '多个用逗号分隔' },
  { prop: 'clazz', label: '处理 Bean', default: 'defaultService' },
  { prop: 'isCryption', label: '需要加解密', options: [{ value: 'true', label: '是' }, { value: 'false', label: '否' }], default: 'false' },
  { prop: 'isOpen', label: '上架', options: [{ value: 'true', label: '上架' }, { value: 'false', label: '下架' }], default: 'false' },
  { prop: 'size', label: '并发上限', type: 'number', default: 100 }
]
</script>
