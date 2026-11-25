<template>
  <div class="page-container">
    <PageHeader :title="title" :description="description">
      <template #actions>
        <el-button v-if="!readonly" type="primary" @click="openAdd">新增</el-button>
      </template>
    </PageHeader>

    <div class="page-card">
      <div class="search-bar">
        <template v-for="f in searchFields" :key="f.prop">
          <el-input
            v-model="query[f.prop]"
            :placeholder="`搜索${f.label}`"
            clearable
            style="width: 180px"
            @keyup.enter="load"
          />
        </template>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border style="width: 100%" size="small">
        <el-table-column v-for="c in columns" :key="c.prop" :prop="c.prop" :label="c.label" :width="c.width" show-overflow-tooltip>
          <template #default="{ row }">
            <template v-if="c.type === 'status'">
              <el-tag :type="statusType(row[c.prop])" size="small">{{ statusText(row[c.prop]) }}</el-tag>
            </template>
            <template v-else-if="c.type === 'boolean'">
              <el-tag :type="String(row[c.prop]) === 'true' ? 'success' : 'info'" size="small">
                {{ String(row[c.prop]) === 'true' ? '是' : '否' }}
              </el-tag>
            </template>
            <template v-else>{{ row[c.prop] }}</template>
          </template>
        </el-table-column>
        <el-table-column v-if="!readonly" label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="toggleStatus(row)">{{ toggleLabel(row) }}</el-button>
            <el-button link type="danger" size="small" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 14px">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="load"
          @size-change="load"
        />
      </div>
    </div>

    <el-dialog v-model="dialog" :title="editing ? '编辑' : '新增'" width="600px">
      <el-form :model="form" label-width="110px" size="small">
        <el-form-item v-for="f in fields" :key="f.prop" :label="f.label">
          <el-select v-if="f.options" v-model="form[f.prop]" style="width: 100%">
            <el-option v-for="o in f.options" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-input-number v-else-if="f.type === 'number'" v-model="form[f.prop]" style="width: 100%" />
          <el-input v-else v-model="form[f.prop]" :placeholder="f.placeholder" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from './PageHeader.vue'

const props = withDefaults(defineProps<{
  title: string
  description?: string
  api: any
  columns: any[]
  fields: any[]
  searchFields?: any[]
  statusKey?: string
  readonly?: boolean
}>(), { searchFields: () => [], statusKey: 'status' })

const loading = ref(false)
const rows = ref<any[]>([])
const total = ref(0)
const query = reactive<any>({ page: 1, size: 10 })
const dialog = ref(false)
const editing = ref(false)
const form = reactive<any>({})
const statusOptions = computed(() => [
  { value: 'true', label: '启用' }, { value: 'false', label: '停用' },
  { value: 'ENABLE', label: '启用' }, { value: 'DISABLE', label: '停用' },
  { value: 'OK', label: '开启' }, { value: 'OFF', label: '关闭' }
])

async function load() {
  loading.value = true
  try {
    const res: any = await props.api.page({ ...query })
    rows.value = res.records || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  props.searchFields.forEach((f: any) => { query[f.prop] = '' })
  query.page = 1
  load()
}

function openAdd() {
  editing.value = false
  Object.keys(form).forEach((k) => delete form[k])
  props.fields.forEach((f: any) => { form[f.prop] = f.default !== undefined ? f.default : '' })
  dialog.value = true
}

function openEdit(row: any) {
  editing.value = true
  Object.keys(form).forEach((k) => delete form[k])
  Object.assign(form, row)
  dialog.value = true
}

async function save() {
  await props.api.save({ ...form })
  dialog.value = false
  ElMessage.success('保存成功')
  load()
}

async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除 ${row.id} 吗？`, '提示', { type: 'warning' })
  await props.api.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

function statusText(v: any) {
  const hit = statusOptions.value.find((o) => o.value === v)
  return hit ? hit.label : v
}
function statusType(v: any) {
  return ['true', 'ENABLE', 'OK'].includes(String(v)) ? 'success' : 'info'
}
function toggleLabel(row: any) {
  return ['true', 'ENABLE', 'OK'].includes(String(row[props.statusKey])) ? '停用' : '启用'
}
async function toggleStatus(row: any) {
  const next = ['true', 'ENABLE', 'OK'].includes(String(row[props.statusKey])) ? 'false' : 'true'
  await props.api.status(row.id, next, props.statusKey)
  ElMessage.success('状态已更新')
  load()
}

onMounted(load)
</script>
