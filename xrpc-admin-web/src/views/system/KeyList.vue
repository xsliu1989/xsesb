<template>
  <CrudPage
    title="密钥管理"
    description="仅展示指纹与密文引用，不展示明文私钥"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { keyApi as api } from '@/api'
const columns = [
  { prop: 'channel', label: '渠道', width: 120 },
  { prop: 'keyType', label: '类型', width: 100 },
  { prop: 'fingerprint', label: '指纹', width: 200 },
  { prop: 'cipherRef', label: '密文引用' },
  { prop: 'expireTime', label: '到期', width: 120 },
  { prop: 'status', label: '状态', width: 100, type: 'status' }
]
const searchFields = [{ prop: 'channel', label: '渠道' }]
const fields = [
  { prop: 'channel', label: '渠道' },
  { prop: 'keyType', label: '类型', options: [{ value: 'SM2', label: 'SM2' }, { value: 'SM4', label: 'SM4' }], default: 'SM2' },
  { prop: 'fingerprint', label: '指纹' },
  { prop: 'cipherRef', label: '密文引用', placeholder: 'kms://...' },
  { prop: 'expireTime', label: '到期时间' },
  { prop: 'status', label: '状态', options: [{ value: 'ENABLE', label: '启用' }, { value: 'DISABLE', label: '停用' }], default: 'ENABLE' }
]
</script>
