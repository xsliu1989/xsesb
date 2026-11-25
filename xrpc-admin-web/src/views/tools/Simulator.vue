<template>
  <div class="page-container">
    <PageHeader title="接口模拟器" description="模拟渠道系统向 ESB 发报，支持 HTTP 与 TCP；报文格式覆盖 JSON / XML / 定长 DC">
      <template #actions>
        <el-button @click="saveCurrent">保存为常用请求</el-button>
        <el-button @click="clearAll">清空</el-button>
      </template>
    </PageHeader>

    <el-tabs v-model="protocol" type="card">
      <el-tab-pane label="HTTP" name="http" />
      <el-tab-pane label="TCP" name="tcp" />
    </el-tabs>

    <el-row :gutter="12">
      <!-- 左：请求 -->
      <el-col :span="14">
        <div class="page-card">
          <div class="section-title">快捷入口</div>
          <div class="preset-bar">
            <template v-if="protocol === 'http'">
              <el-button size="small" @click="applyHttpPreset('JSON')">JSON 入口</el-button>
              <el-button size="small" @click="applyHttpPreset('XML')">XML 入口</el-button>
              <el-button size="small" @click="applyHttpPreset('DC')">定长 DC 入口</el-button>
              <el-button size="small" @click="applyHttpPreset('DEFAULT')">通用入口</el-button>
              <el-button size="small" type="primary" plain @click="applyStubPreset">挡板联调 TRANNO001</el-button>
            </template>
            <template v-else>
              <el-button size="small" @click="applyTcpPreset('JSON')">TCP JSON :9999</el-button>
              <el-button size="small" @click="applyTcpPreset('XML')">TCP XML :9997</el-button>
              <el-button size="small" @click="applyTcpPreset('DC')">TCP DC :9998</el-button>
            </template>
          </div>

          <div class="section-title" style="margin-top: 14px">连接设置</div>
          <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
            title="这里填的是你的 ESB 项目地址（accept-http，默认 9300），不是 xrpc-admin。xrpc-admin 只做转发以避开浏览器跨域；ESB 收到报文后再调用挡板服务。" />
          <template v-if="protocol === 'http'">
            <el-form label-width="110px" size="small">
              <el-form-item label="请求地址">
                <el-input v-model="http.url" placeholder="http://localhost:9300/controller/jsonHandler" />
              </el-form-item>
              <el-row :gutter="10">
                <el-col :span="16">
                  <el-form-item label="Content-Type">
                    <el-input v-model="http.contentType" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="超时(ms)">
                    <el-input-number v-model="http.timeout" :min="100" :max="60000" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </template>
          <template v-else>
            <el-form label-width="110px" size="small">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="主机">
                    <el-input v-model="tcp.host" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="端口">
                    <el-input-number v-model="tcp.port" :min="1" :max="65535" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="8">
                  <el-form-item label="字符集">
                    <el-select v-model="tcp.charset" style="width: 100%">
                      <el-option label="UTF-8" value="UTF-8" />
                      <el-option label="GBK" value="GBK" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="长度位宽">
                    <el-input-number v-model="tcp.lengthWidth" :min="1" :max="12" style="width: 100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="超时(ms)">
                    <el-input-number v-model="tcp.timeout" :min="100" :max="60000" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="响应读取">
                <el-select v-model="tcp.readMode" style="width: 100%">
                  <el-option label="自动兼容（推荐）" value="auto" />
                  <el-option label="强制按长度头" value="length" />
                  <el-option label="原始报文" value="raw" />
                </el-select>
              </el-form-item>
            </el-form>
          </template>

          <div class="section-title" style="margin-top: 14px">渠道报文生成（可选）</div>
          <el-form label-width="90px" size="small">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="交易码">
                  <el-input v-model="builder.tranNo" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="渠道">
                  <el-input v-model="builder.channel" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="业务数据">
              <el-input v-model="builder.data" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="流水号">
              <el-input v-model="builder.nonce">
                <template #append>
                  <el-button @click="builder.nonce = genNonce()">生成</el-button>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="时间戳">
              <el-input v-model="builder.timestamp">
                <template #append>
                  <el-button @click="builder.timestamp = genTimestamp()">生成</el-button>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="">
              <el-button size="small" @click="applyBuilder('json')">生成 JSON 报文</el-button>
              <el-button size="small" @click="applyBuilder('xml')">生成 XML 报文</el-button>
              <el-button size="small" @click="applyBuilder('dc')">生成定长报文</el-button>
            </el-form-item>
          </el-form>

          <div class="section-title" style="margin-top: 14px">
            请求报文
            <span class="text-muted">（{{ bodyLength }} 字符）</span>
          </div>
          <el-input v-model="currentBody" type="textarea" :rows="12" class="code-input" spellcheck="false" />
          <div style="margin-top: 12px">
            <el-button type="primary" :loading="sending" @click="send">发送报文</el-button>
            <span class="text-muted" style="margin-left: 10px">Ctrl + Enter 也可发送</span>
          </div>
        </div>
      </el-col>

      <!-- 右：响应 -->
      <el-col :span="10">
        <div class="page-card">
          <div class="section-title">响应结果</div>
          <template v-if="response">
            <el-alert
              :title="response.success ? '请求成功' : '请求失败'"
              :type="response.success ? 'success' : 'error'"
              :description="response.error || ''"
              :closable="false"
              show-icon
              style="margin-bottom: 12px"
            />
            <el-descriptions :column="2" border size="small" style="margin-bottom: 12px">
              <el-descriptions-item label="状态">{{ response.status }}</el-descriptions-item>
              <el-descriptions-item label="耗时">{{ response.costMs }} ms</el-descriptions-item>
            </el-descriptions>
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px">
              <span class="section-title" style="margin: 0">响应报文</span>
              <div>
                <el-button link type="primary" size="small" @click="pretty = !pretty">
                  {{ pretty ? '原始' : '格式化 JSON' }}
                </el-button>
                <el-button link type="primary" size="small" @click="copy(response.body)">复制</el-button>
              </div>
            </div>
            <pre class="code-block response-block">{{ displayBody }}</pre>
            <div v-if="response.headers" class="section-title" style="margin-top: 12px">响应元信息</div>
            <pre v-if="response.headers" class="code-block">{{ response.headers }}</pre>
          </template>
          <el-empty v-else description="发送报文后在这里查看响应" />

          <div class="section-title" style="margin-top: 16px">常用请求（本地保存）</div>
          <el-table :data="history" size="small" border v-if="history.length">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="protocol" label="协议" width="70" />
            <el-table-column label="操作" width="110">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="loadRequest(row)">载入</el-button>
                <el-button link type="danger" size="small" @click="removeHistory(row)">删</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无保存的请求" :image-size="50" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { simulatorApi } from '@/api'

const HISTORY_KEY = 'xrpc-simulator-history'

const HTTP_PRESETS: any = {
  JSON: { url: 'http://localhost:9300/controller/jsonHandler', contentType: 'application/json;charset=UTF-8' },
  XML: { url: 'http://localhost:9300/controller/xmlHandler', contentType: 'application/xml;charset=UTF-8' },
  DC: { url: 'http://localhost:9300/controller/fixedLengthHandler', contentType: 'text/plain;charset=UTF-8' },
  DEFAULT: { url: 'http://localhost:9300/controller/defaultHandler', contentType: 'application/json;charset=UTF-8' }
}
const TCP_PRESETS: any = {
  JSON: { host: '127.0.0.1', port: 9999, charset: 'UTF-8' },
  XML: { host: '127.0.0.1', port: 9997, charset: 'UTF-8' },
  DC: { host: '127.0.0.1', port: 9998, charset: 'GBK' }
}

const protocol = ref<'http' | 'tcp'>('http')
const http = reactive({ url: HTTP_PRESETS.JSON.url, contentType: HTTP_PRESETS.JSON.contentType, timeout: 5000, body: '' })
const tcp = reactive({ host: '127.0.0.1', port: 9999, charset: 'UTF-8', lengthWidth: 6, readMode: 'auto', timeout: 5000, body: '' })
const builder = reactive({ tranNo: 'TRANNO001', channel: 'CH01', nonce: '', timestamp: '', data: '{"transaction":{"header":{"msg":{"seqNb":"111106500000497"}},"body":{"request":{"orderId":"O123"}}}}' })
const response = ref<any>(null)
const sending = ref(false)
const pretty = ref(false)
const history = ref<any[]>([])

const currentBody = computed({
  get: () => protocol.value === 'http' ? http.body : tcp.body,
  set: (v: string) => { if (protocol.value === 'http') http.body = v; else tcp.body = v }
})
const bodyLength = computed(() => currentBody.value.length)
const displayBody = computed(() => {
  if (!response.value?.body) return ''
  if (!pretty.value) return response.value.body
  try {
    const parsed = JSON.parse(response.value.body)
    return JSON.stringify(parsed, null, 2)
  } catch {
    // 尝试截取长度头之后的 JSON
    const m = String(response.value.body).match(/^\d{4,}([\s\S]*)$/)
    if (m) { try { return JSON.stringify(JSON.parse(m[1]), null, 2) } catch { /* ignore */ } }
    return response.value.body
  }
})

function pad(n: number, len = 2) { return String(n).padStart(len, '0') }
function genNonce() {
  const d = new Date()
  return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}${pad(d.getHours())}${pad(d.getMinutes())}${pad(d.getSeconds())}${pad(d.getMilliseconds(), 3)}${Math.floor(Math.random() * 100000).toString().padStart(5, '0')}`
}
function genTimestamp() {
  const d = new Date()
  return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}${pad(d.getHours())}${pad(d.getMinutes())}${pad(d.getSeconds())}${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}`
}

function applyHttpPreset(key: string) {
  const p = HTTP_PRESETS[key]
  http.url = p.url
  http.contentType = p.contentType
  if (!builder.nonce) builder.nonce = genNonce()
  if (!builder.timestamp) builder.timestamp = genTimestamp()
}
function applyTcpPreset(key: string) {
  const p = TCP_PRESETS[key]
  tcp.host = p.host
  tcp.port = p.port
  tcp.charset = p.charset
  tcp.lengthWidth = 6
}

/** 一键构造发往 ESB 的渠道报文，配合 sql/06-挡板联调-TRANNO001.sql 使用 */
function applyStubPreset() {
  protocol.value = 'http'
  applyHttpPreset('JSON')
  builder.tranNo = 'TRANNO001'
  builder.channel = 'CH01'
  builder.data = '{"transaction":{"header":{"msg":{"seqNb":"111106500000497","sndDt":"20220113"}},"body":{"request":{"orderId":"O123"}}}}'
  builder.nonce = genNonce()
  builder.timestamp = genTimestamp()
  applyBuilder('json')
  response.value = null
  ElMessage.success('已填入挡板联调报文，点击「发送报文」即可')
}

function innerData(): string {
  try { return JSON.stringify(JSON.parse(builder.data)) } catch { return builder.data }
}
function applyBuilder(type: 'json' | 'xml' | 'dc') {
  if (!builder.nonce) builder.nonce = genNonce()
  if (!builder.timestamp) builder.timestamp = genTimestamp()
  if (type === 'json') {
    currentBody.value = JSON.stringify({
      tranNo: builder.tranNo, channel: builder.channel, nonce: builder.nonce,
      timestamp: builder.timestamp, digestAlgorithm: 'SM3', data: innerData()
    }, null, 2)
  } else if (type === 'xml') {
    currentBody.value =
`<?xml version="1.0" encoding="UTF-8"?>
<CustomerReq>
  <tranNo>${builder.tranNo}</tranNo>
  <channel>${builder.channel}</channel>
  <nonce>${builder.nonce}</nonce>
  <timestamp>${builder.timestamp}</timestamp>
  <data>${builder.data}</data>
</CustomerReq>`
  } else {
    const no = builder.tranNo.padEnd(10, ' ')
    currentBody.value = no + builder.data
  }
}

async function send() {
  if (!currentBody.value.trim()) { ElMessage.warning('请先填写请求报文'); return }
  sending.value = true
  response.value = null
  try {
    if (protocol.value === 'http') {
      response.value = await simulatorApi.http({
        url: http.url, data: currentBody.value, contentType: http.contentType,
        charset: http.contentType.includes('GBK') ? 'GBK' : 'UTF-8', timeout: http.timeout
      })
    } else {
      response.value = await simulatorApi.tcp({
        host: tcp.host, port: tcp.port, charset: tcp.charset,
        lengthWidth: tcp.lengthWidth, readMode: tcp.readMode,
        timeout: tcp.timeout, data: currentBody.value
      })
    }
  } catch (e: any) {
    response.value = { success: false, status: 0, body: '', costMs: 0, error: e?.message || '发送失败', headers: null }
  } finally {
    sending.value = false
  }
}

function loadHistories() {
  try { history.value = JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]') } catch { history.value = [] }
}
function persist() { localStorage.setItem(HISTORY_KEY, JSON.stringify(history.value)) }
async function saveCurrent() {
  const name = (await ElMessageBox.prompt('给这个请求起个名字', '保存常用请求', { inputPlaceholder: '例如：渠道A-下单-JSON' })).value
  if (!name) return
  history.value.unshift({
    id: Date.now(), name, protocol: protocol.value,
    url: http.url, contentType: http.contentType,
    host: tcp.host, port: tcp.port, charset: tcp.charset, lengthWidth: tcp.lengthWidth, readMode: tcp.readMode,
    body: currentBody.value
  })
  history.value = history.value.slice(0, 30)
  persist()
  ElMessage.success('已保存')
}
function loadRequest(row: any) {
  protocol.value = row.protocol
  http.url = row.url || http.url
  http.contentType = row.contentType || http.contentType
  tcp.host = row.host || tcp.host
  tcp.port = row.port || tcp.port
  tcp.charset = row.charset || tcp.charset
  tcp.lengthWidth = row.lengthWidth || tcp.lengthWidth
  tcp.readMode = row.readMode || tcp.readMode
  currentBody.value = row.body || ''
  ElMessage.success('已载入')
}
async function removeHistory(row: any) {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  history.value = history.value.filter((h) => h.id !== row.id)
  persist()
}
function clearAll() {
  currentBody.value = ''
  response.value = null
}
async function copy(text: string) {
  try { await navigator.clipboard.writeText(text); ElMessage.success('已复制') }
  catch { ElMessage.warning('浏览器不允许复制，请手动选择') }
}
function onKeydown(e: KeyboardEvent) {
  if (e.ctrlKey && e.key === 'Enter') { e.preventDefault(); send() }
}

onMounted(() => {
  loadHistories()
  builder.nonce = genNonce()
  builder.timestamp = genTimestamp()
  window.addEventListener('keydown', onKeydown)
})
onUnmounted(() => window.removeEventListener('keydown', onKeydown))
</script>

<style scoped>
.section-title { font-weight: 600; font-size: 13px; margin-bottom: 10px; }
.preset-bar { display: flex; gap: 8px; flex-wrap: wrap; }
.code-input :deep(textarea) { font-family: Consolas, Monaco, monospace; font-size: 12px; }
.response-block { max-height: 400px; margin: 0; }
</style>
