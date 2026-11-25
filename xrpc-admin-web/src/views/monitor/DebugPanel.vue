<template>
  <div class="page-container">
    <PageHeader title="在线调试" description="输入渠道报文，观察四段式转换的中间结果" />
    <el-row :gutter="12">
      <el-col :span="12">
        <div class="page-card">
          <div class="panel-title">请求输入</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="交易码"><el-input v-model="form.tranNo" placeholder="TRANNO001" /></el-form-item>
            <el-form-item label="报文类型">
              <el-select v-model="form.channelType" style="width: 100%">
                <el-option label="JSON" value="JSON" /><el-option label="XML" value="XML" /><el-option label="DC" value="DC" />
              </el-select>
            </el-form-item>
            <el-form-item label="报文内容">
              <el-input v-model="form.data" type="textarea" :rows="12" />
            </el-form-item>
            <el-form-item label="真实出站">
              <el-switch v-model="form.dryRun" active-text="仅本地转换" inactive-text="调用数据源" />
            </el-form-item>
            <el-button type="primary" :loading="loading" @click="run">执行</el-button>
          </el-form>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card">
          <div class="panel-title">
            转换结果
            <span v-if="result" class="text-muted">耗时 {{ result.costMs }} ms</span>
          </div>
          <el-collapse v-if="result">
            <el-collapse-item v-for="s in result.stages || []" :key="s.name" :title="s.name" :name="s.name">
              <pre class="code-block">{{ pretty(s.content) }}</pre>
            </el-collapse-item>
          </el-collapse>
          <el-empty v-else description="点击执行查看结果" />
          <template v-if="result">
            <h4>最终响应</h4>
            <pre class="code-block">{{ pretty(result.response) }}</pre>
          </template>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { debugApi } from '@/api'
import { pretty } from '@/utils/format'

const form = reactive<any>({
  tranNo: 'TRANNO001', channelType: 'JSON', dryRun: true,
  data: '{"transaction":{"header":{"msg":{"seqNb":"111106500000497"}},"body":{"request":{"orderId":"O123"}}}}'
})
const loading = ref(false)
const result = ref<any>(null)

async function run() {
  loading.value = true
  try {
    result.value = await debugApi.invoke({ ...form })
    ElMessage.success('执行完成')
  } catch (e: any) { ElMessage.error(e?.message || '执行失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.panel-title { font-weight: 600; font-size: 13px; margin-bottom: 10px; }
</style>
