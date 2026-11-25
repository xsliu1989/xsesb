<template>
  <CrudPage
    title="用户管理"
    description="控制台用户与角色分配"
    :api="api"
    :columns="columns"
    :fields="fields"
    :search-fields="searchFields"
    status-key="status"
  />
</template>

<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { userApi as api } from '@/api'
const columns = [
  { prop: 'username', label: '账号', width: 140 },
  { prop: 'realName', label: '姓名', width: 140 },
  { prop: 'roles', label: '角色', width: 160 },
  { prop: 'status', label: '状态', width: 100, type: 'status' }
]
const searchFields = [{ prop: 'username', label: '账号' }]
const fields = [
  { prop: 'username', label: '账号' }, { prop: 'realName', label: '姓名' },
  { prop: 'roles', label: '角色', placeholder: 'ADMIN,CONFIG' },
  { prop: 'status', label: '状态', options: [{ value: 'ENABLE', label: '启用' }, { value: 'DISABLE', label: '停用' }], default: 'ENABLE' }
]
</script>
