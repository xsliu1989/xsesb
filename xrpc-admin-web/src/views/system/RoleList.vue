<template>
  <div class="page-container">
    <PageHeader title="角色权限" description="内置五种角色，按钮级权限由后端 @PreAuthorize 控制" />
    <div class="page-card">
      <el-table :data="rows" border size="small" v-loading="loading">
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="remark" label="说明" />
        <el-table-column label="权限" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="open(row)">查看权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="visible" :title="`${current?.roleName} 权限`" width="500px">
      <el-tree :data="perms" :props="{ label: 'permName', children: 'children' }" default-expand-all show-checkbox />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { roleApi } from '@/api'

const rows = ref<any[]>([])
const loading = ref(false)
const visible = ref(false)
const current = ref<any>(null)
const perms = ref<any[]>([
  { permName: '配置中心', children: [
    { permName: '渠道系统信息' }, { permName: '渠道接口' }, { permName: '数据源系统信息' },
    { permName: 'Tag 配置' }, { permName: '字段映射' }, { permName: '发布管理' }
  ] },
  { permName: '运行监控', children: [{ permName: '交易日志' }, { permName: '在线调试' }, { permName: '缓存状态' }] },
  { permName: '系统管理', children: [{ permName: '用户管理' }, { permName: '角色权限' }, { permName: '操作审计' }] }
])

async function load() {
  loading.value = true
  try { rows.value = (await roleApi.list({ size: 10000 })).records || [] } finally { loading.value = false }
}
function open(row: any) { current.value = row; visible.value = true }

onMounted(load)
</script>
