<template>
  <div class="page-container">
    <PageHeader title="工作台" description="ESB 配置与运行概览" />
    <el-row :gutter="12">
      <el-col :span="4" v-for="c in cards" :key="c.title">
        <el-card shadow="hover">
          <div class="metric">
            <div class="value">{{ c.value }}</div>
            <div class="title">{{ c.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" style="margin-top: 12px">
      <el-col :span="12">
        <el-card header="异常 Top">
          <el-table :data="data.errorTop || []" size="small">
            <el-table-column prop="code" label="错误码" />
            <el-table-column prop="count" label="次数" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="最近变更">
          <el-timeline>
            <el-timeline-item v-for="v in data.recentChanges || []" :key="v.id" :timestamp="v.publishTime || ''" placement="top">
              {{ v.targetType }} / {{ v.targetKey }} · {{ v.status }} · {{ v.draftBy }}
            </el-timeline-item>
            <el-empty v-if="!(data.recentChanges || []).length" description="暂无变更" :image-size="50" />
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { dashboardApi } from '@/api'

const data = ref<any>({})
const cards = computed(() => [
  { title: '渠道系统', value: data.value.channelCount || 0 },
  { title: '渠道接口', value: data.value.customerTranCount || 0 },
  { title: '数据源接口', value: data.value.providerTranCount || 0 },
  { title: '已上架接口', value: data.value.openedCount || 0 },
  { title: '今日调用量', value: data.value.todayCalls || 0 },
  { title: '成功率', value: data.value.successRate || '-' }
])

onMounted(async () => { data.value = await dashboardApi.overview() })
</script>

<style scoped>
.metric { text-align: center; padding: 8px 0; }
.metric .value { font-size: 26px; font-weight: 600; color: #409eff; }
.metric .title { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
