<template>
  <div class="tag-editor">
    <div class="toolbar">
      <el-button size="small" @click="addRoot" :disabled="readonly">新增根节点</el-button>
      <el-button size="small" @click="addChild" :disabled="!current || readonly">新增子节点</el-button>
      <el-button size="small" @click="removeCurrent" :disabled="!current || readonly">删除</el-button>
      <el-button size="small" @click="inferVisible = true" :disabled="readonly">从样例推断</el-button>
      <el-button size="small" type="primary" @click="validate">校验</el-button>
      <span class="text-muted">共 {{ flat.length }} 个节点 · 拖拽可调整顺序</span>
    </div>

    <el-row :gutter="12">
      <el-col :span="12">
        <div class="panel">
          <div class="panel-title">Tag 结构树</div>
          <el-tree
            :data="tree"
            node-key="id"
            draggable
            :props="{ label: 'tagName', children: 'children' }"
            :default-expand-all="true"
            highlight-current
            @node-click="onNodeClick"
            @node-drop="onDrop"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ data.tagName || '(未命名)' }}</span>
                <el-tag size="small" type="info" effect="plain">{{ data.tagType }}</el-tag>
                <span v-if="data.tagflag" class="flag">{{ data.tagflag }}</span>
              </span>
            </template>
          </el-tree>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="panel">
          <div class="panel-title">属性面板</div>
          <el-form v-if="current" :model="current" label-width="100px" size="small">
            <el-form-item label="标签名"><el-input v-model="current.tagName" :disabled="readonly" @change="commit" /></el-form-item>
            <el-form-item label="父路径"><el-input v-model="current.parentTag" :disabled="readonly" @change="commit" /></el-form-item>
            <el-form-item label="映射路径"><el-input v-model="current.tagMapping" :disabled="readonly" placeholder="transaction.header.msg.seqNb" @change="commit" /></el-form-item>
            <el-form-item label="类型">
              <el-select v-model="current.tagType" :disabled="readonly" style="width: 100%" @change="commit">
                <el-option v-for="t in TAG_TYPES" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
            <el-form-item label="语义标记">
              <el-select v-model="current.tagflag" :disabled="readonly" clearable style="width: 100%" @change="commit">
                <el-option v-for="t in TAG_FLAGS" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
            <el-form-item label="固定值"><el-input v-model="current.tagValue" :disabled="readonly" @change="commit" /></el-form-item>
            <el-form-item label="排序"><el-input-number v-model="current.sort" :min="1" :disabled="readonly" @change="commit" /></el-form-item>
            <el-form-item label="定长长度"><el-input-number v-model="current.taglength" :min="1" :disabled="protocol !== 'dc' || readonly" @change="commit" /></el-form-item>
          </el-form>
          <el-empty v-else description="请选择一个节点" :image-size="60" />
        </div>
      </el-col>
    </el-row>

    <div v-if="issues.length" class="panel" style="margin-top: 12px">
      <div class="panel-title">校验结果</div>
      <el-alert v-for="(e, i) in issues" :key="i" :title="e" type="error" :closable="false" show-icon style="margin-bottom: 6px" />
    </div>

    <el-dialog v-model="inferVisible" title="从样例报文推断 Tag 骨架" width="600px">
      <el-alert type="info" :closable="false" title="仅支持 JSON；XML 请先转换为 JSON。推断结果请人工确认后再保存。" style="margin-bottom: 10px" />
      <el-input v-model="sample" type="textarea" :rows="10" placeholder='{"transaction":{"header":{"msg":{"seqNb":"001"}}}}' />
      <template #footer>
        <el-button @click="inferVisible = false">取消</el-button>
        <el-button type="primary" @click="doInfer">推断</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  buildTagTree, flattenTagTree, findNode, removeNode, reindexSort,
  detectIssues, inferFromSample, clone, type TagNode
} from '@/utils/tagTree'

const TAG_TYPES = ['String', 'Object', 'List', 'SizeList', 'NoSizeList']
const TAG_FLAGS = ['TOP', 'code', 'message', 'nonce', 'timestamp', 'channel', 'ListSizeCount']

const props = withDefaults(defineProps<{
  modelValue: TagNode[]
  ownerKey: string
  protocol?: string
  readonly?: boolean
}>(), { protocol: 'json', readonly: false })

const emit = defineEmits<{
  (e: 'update:modelValue', value: TagNode[]): void
  (e: 'validate', value: any): void
}>()

const tree = ref<TagNode[]>([])
const current = ref<TagNode | null>(null)
const issues = ref<string[]>([])
const inferVisible = ref(false)
const sample = ref('')

const flat = computed(() => flattenTagTree(tree.value, props.ownerKey))

watch(() => props.modelValue, (v) => {
  tree.value = buildTagTree(v || [], props.ownerKey)
  current.value = null
}, { immediate: true, deep: true })

function commit() {
  const next = reindexSort(flattenTagTree(tree.value, props.ownerKey))
  emit('update:modelValue', next)
}

function onNodeClick(node: TagNode) { current.value = node }

function nextId() {
  return Math.max(0, ...props.modelValue.map((t) => Number(t.id) || 0)) + 1
}

function addRoot() {
  tree.value.push({ id: nextId(), tagName: 'newNode', parentTag: props.ownerKey, tagMapping: '', tagType: 'String', sort: tree.value.length + 1 })
  commit()
  ElMessage.success('已新增根节点')
}

function addChild() {
  if (!current.value) return
  if (!current.value.children) current.value.children = []
  const parent = current.value
  const parentPath = parent.parentTag === props.ownerKey ? parent.tagName : `${parent.parentTag}.${parent.tagName}`
  parent.children.push({ id: nextId(), tagName: 'newChild', parentTag: parentPath, tagMapping: '', tagType: 'String', sort: parent.children.length + 1 })
  commit()
}

function removeCurrent() {
  if (!current.value) return
  tree.value = removeNode(tree.value, current.value.id!)
  current.value = null
  commit()
}

function onDrop() {
  tree.value = reindexSort(tree.value)
  commit()
}

function validate() {
  issues.value = detectIssues(flat.value, props.ownerKey)
  emit('validate', { issues: issues.value, tags: flat.value })
  if (!issues.value.length) ElMessage.success('校验通过')
  else ElMessage.error(`发现 ${issues.value.length} 个问题`)
}

function doInfer() {
  const list = inferFromSample(sample.value)
  if (!list.length) { ElMessage.error('样例解析失败，仅支持 JSON'); return }
  list.forEach((t, i) => {
    t.id = nextId() + i
    if (!t.parentTag) t.parentTag = props.ownerKey
    else if (!t.parentTag.startsWith(props.ownerKey)) t.parentTag = `${props.ownerKey}.${t.parentTag}`
  })
  tree.value = buildTagTree(list, props.ownerKey)
  emit('update:modelValue', flattenTagTree(tree.value, props.ownerKey))
  inferVisible.value = false
  ElMessage.success(`已推断 ${list.length} 个节点`)
}

defineExpose({ validate, current: () => current.value, tags: () => flat.value })
</script>

<style scoped>
.toolbar { display: flex; gap: 8px; align-items: center; margin-bottom: 12px; flex-wrap: wrap; }
.panel { background: #fff; border: 1px solid #e4e7ed; border-radius: 4px; padding: 12px; min-height: 420px; }
.panel-title { font-weight: 600; margin-bottom: 10px; font-size: 13px; }
.tree-node { display: flex; align-items: center; gap: 8px; }
.flag { color: #e6a23c; font-size: 11px; }
</style>
