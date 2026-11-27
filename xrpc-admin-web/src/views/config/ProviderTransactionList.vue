<template>
  <CrudPage
    title="数据源系统信息"
    description="维护目标系统接口、协议与地址（对应 esb_provider_transaction_info）"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { providerTranApi as api } from '@/api'
const columns = [
  { prop: 'providerTranNo', label: '目标交易码', width: 130 },
  { prop: 'customerTranNo', label: '关联渠道交易码', width: 150 },
  { prop: 'sysName', label: '目标系统', width: 130 },
  { prop: 'dataType', label: '协议', width: 90 },
  { prop: 'codeType', label: '编码', width: 90 },
  { prop: 'sendUrl', label: '发送地址' }
]
const searchFields = [{ prop: 'providerTranNo', label: '目标交易码' }, { prop: 'customerTranNo', label: '渠道交易码' }]
const fields = [
  { prop: 'providerTranNo', label: '目标交易码', placeholder: 'P001' },
  { prop: 'customerTranNo', label: '关联渠道交易码', placeholder: 'TRANNO001' },
  { prop: 'sysName', label: '目标系统' },
  { prop: 'dataType', label: '协议', options: [{ value: 'json', label: 'json' }, { value: 'xml', label: 'xml' }, { value: 'dc', label: 'dc（定长）' }], default: 'json' },
  { prop: 'codeType', label: '编码', options: [{ value: 'UTF-8', label: 'UTF-8' }], default: 'UTF-8' },
  { prop: 'sendUrl', label: '发送地址', placeholder: 'http://host:port/path' }
]
</script>
