import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { loadDb, saveDb } from './mockDb'
import { buildTagTree, detectIssues, inferFromSample, type TagNode } from '@/utils/tagTree'

const resources: Record<string, string> = {
  channels: 'channels',
  'customer-transactions': 'customerTransactions',
  'provider-transactions': 'providerTransactions',
  samples: 'samples',
  'error-codes': 'errorCodes',
  cport: 'cport',
  keys: 'keys',
  users: 'users',
  roles: 'roles',
  'audit-logs': 'auditLogs'
}

function ok(config: InternalAxiosRequestConfig, data: any): AxiosResponse {
  return { data: { code: 0, message: 'ok', data }, status: 200, statusText: 'OK', headers: {}, config }
}

function body(config: InternalAxiosRequestConfig): any {
  try { return typeof config.data === 'string' ? JSON.parse(config.data) : config.data || {} } catch { return {} }
}

function nextId(rows: any[]) {
  return rows.length ? Math.max(...rows.map((r) => Number(r.id) || 0)) + 1 : 1
}

function pageRows(rows: any[], params: any) {
  const p = Number(params?.page || 1)
  const s = Number(params?.size || 10)
  let out = rows
  Object.keys(params || {}).forEach((k) => {
    if (['page', 'size'].includes(k)) return
    const v = params[k]
    if (v === undefined || v === '' || v === null) return
    out = out.filter((r) => String(r[k] ?? '').toLowerCase().includes(String(v).toLowerCase()))
  })
  return { page: p, size: s, total: out.length, records: out.slice((p - 1) * s, p * s) }
}

function audit(db: any, module: string, action: string, target: string, before: any, after: any) {
  db.auditLogs.unshift({
    id: nextId(db.auditLogs), operator: 'admin', operateTime: new Date().toISOString(),
    ip: '127.0.0.1', module, action, target,
    beforeJson: before ? JSON.stringify(before) : '', afterJson: after ? JSON.stringify(after) : '', result: 'SUCCESS'
  })
}

const SAMPLE = '{"transaction":{"header":{"msg":{"seqNb":"111106500000497","sndDt":"20220113"}},"body":{"request":{"orderId":"O123"}}}}'

export async function mockAdapter(config: InternalAxiosRequestConfig): Promise<AxiosResponse> {
  const db = loadDb()
  const method = (config.method || 'get').toUpperCase()
  let url = (config.url || '').replace(/^\/admin-api/, '')
  const params = config.params || {}
  const data = body(config)
  const result = () => { saveDb(db); return ok(config, undefined) }

  // ---- 认证 ----
  if (url === '/auth/login' && method === 'POST') {
    return ok(config, { token: 'mock-token-' + Date.now(), username: data.username || 'admin', realName: '演示用户' })
  }
  if (url === '/auth/me') return ok(config, { username: 'admin', realName: '演示用户' })
  if (url === '/auth/permissions') return ok(config, ['*'])
  if (url === '/auth/logout') return ok(config, true)

  // ---- 看板 ----
  if (url === '/dashboard/overview') {
    return ok(config, {
      channelCount: db.channels.length,
      customerTranCount: db.customerTransactions.length,
      providerTranCount: db.providerTransactions.length,
      openedCount: db.customerTransactions.filter((t: any) => t.isOpen === 'true').length,
      todayCalls: 1286,
      successRate: '98.7%',
      tagCount: db.tags.length,
      versionCount: db.versions.length,
      recentChanges: db.versions.slice(0, 5),
      errorTop: [
        { code: '06987', count: 12 }, { code: '98935', count: 7 }, { code: '863', count: 5 }
      ]
    })
  }

  // ---- Tag 专用接口（必须在通用资源之前） ----
  if (url === '/tags' && method === 'GET') {
    return ok(config, db.tags.filter((t: any) => t.ownerType === params.ownerType && t.ownerKey === params.ownerKey))
  }
  if (url === '/tags/validate' && method === 'POST') {
    const issues = detectIssues(data.tags || [], data.ownerKey || '')
    const sampleObj = (() => { try { return JSON.parse(data.sample || SAMPLE) } catch { return {} } })()
    const preview = (data.tags || []).map((t: TagNode) => {
      let v: any = sampleObj
      String(t.tagMapping || '').split('.').forEach((seg) => { v = v && typeof v === 'object' ? v[seg] : undefined })
      return { tagId: t.id, path: t.tagMapping, value: v === undefined ? '' : String(v) }
    })
    return ok(config, { passed: issues.length === 0, errors: issues.map((m) => ({ message: m })), warnings: [], preview })
  }
  if (url === '/tags/infer' && method === 'POST') {
    const inferred = inferFromSample(data.sample || SAMPLE)
    inferred.forEach((t) => { t.id = nextId(db.tags) + (t.id || 0); t.tagName = t.tagName || 'root' })
    return ok(config, inferred.slice(0, 200))
  }
  if (url === '/tags/copy' && method === 'POST') {
    const rows = db.tags.filter((t: any) => t.ownerType === data.fromOwnerType && t.ownerKey === data.fromOwnerKey)
    rows.forEach((t: any) => {
      db.tags.push({ ...t, id: nextId(db.tags), ownerKey: data.toOwnerKey, parentTag: t.parentTag.replace(data.fromOwnerKey, data.toOwnerKey) })
    })
    saveDb(db)
    return ok(config, rows.length)
  }
  if (url === '/tags/batch' && method === 'PUT') {
    db.tags = db.tags.filter((t: any) => !(t.ownerType === data.ownerType && t.ownerKey === data.ownerKey))
    ;(data.tags || []).forEach((t: any) => db.tags.push({ ...t, ownerType: data.ownerType, ownerKey: data.ownerKey }))
    audit(db, 'Tag 配置', '批量保存', `${data.ownerType}/${data.ownerKey}`, null, data.tags)
    saveDb(db)
    return ok(config, { saved: (data.tags || []).length })
  }

  // ---- 映射 ----
  if (url === '/mapping/compare') {
    const p = db.providerTransactions.find((x: any) => x.customerTranNo === params.tranNo)
    const providerKey = p?.providerTranNo || params.tranNo
    const req = db.tags.filter((t: any) => t.ownerType === 'CUSTOMER_REQUEST' && t.ownerKey === params.tranNo)
    const pro = db.tags.filter((t: any) => t.ownerType === 'PROVIDER_REQUEST' && t.ownerKey === providerKey)
    const reqMap = new Map(req.map((t: any) => [t.tagMapping, t]))
    const proMap = new Map(pro.map((t: any) => [t.tagMapping, t]))
    const rows = Array.from(new Set([...reqMap.keys(), ...proMap.keys()])).sort().map((k) => ({
      mapping: k,
      customer: reqMap.get(k) || null,
      provider: proMap.get(k) || null,
      status: reqMap.has(k) && proMap.has(k) ? 'MAPPED' : (reqMap.has(k) ? 'PROVIDER_MISSING' : 'CUSTOMER_MISSING')
    }))
    return ok(config, { rows, tranNo: params.tranNo })
  }

  // ---- 版本 ----
  if (url === '/versions' && method === 'GET') return ok(config, pageRows(db.versions, params))
  if (url === '/versions/draft' && method === 'POST') {
    const v = {
      id: nextId(db.versions), env: data.env || 'dev', targetType: data.targetType, targetKey: data.targetKey,
      versionNo: db.versions.filter((x: any) => x.targetKey === data.targetKey).length + 1,
      status: 'DRAFT', contentJson: JSON.stringify(data.content), diffJson: '', draftBy: 'admin',
      approveBy: '', publishTime: '', remark: data.remark || ''
    }
    db.versions.unshift(v); saveDb(db); return ok(config, v)
  }
  const versionAction = url.match(/^\/versions\/(\d+)\/(validate|diff|approve|publish|rollback)$/)
  if (versionAction) {
    const v = db.versions.find((x: any) => x.id === Number(versionAction[1]))
    if (!v) return ok(config, null)
    const action = versionAction[2]
    if (action === 'diff') {
      const before = JSON.parse(v.contentJson || '[]')
      return ok(config, { added: before, removed: [], modified: [] })
    }
    if (action === 'approve') v.status = 'APPROVED'
    if (action === 'publish') {
      v.status = 'PUBLISHED'
      v.publishTime = new Date().toISOString()
      v.approveBy = 'admin'
      const content = JSON.parse(v.contentJson || '[]')
      if (v.targetType === 'CUSTOMER_REQUEST' || v.targetType === 'CUSTOMER_RESPONSE' || v.targetType === 'PROVIDER_REQUEST' || v.targetType === 'PROVIDER_RESPONSE') {
        db.tags = db.tags.filter((t: any) => !(t.ownerType === v.targetType && t.ownerKey === v.targetKey))
        content.forEach((t: any) => db.tags.push({ ...t, ownerType: v.targetType, ownerKey: v.targetKey }))
      }
      audit(db, '发布管理', '发布', v.targetKey, null, v.contentJson)
    }
    if (action === 'rollback') {
      const old = db.versions.find((x: any) => x.targetKey === v.targetKey && x.status === 'PUBLISHED' && x.id !== v.id)
      db.versions.unshift({
        id: nextId(db.versions), env: v.env, targetType: v.targetType, targetKey: v.targetKey,
        versionNo: v.versionNo + 1, status: 'PUBLISHED', contentJson: old ? old.contentJson : v.contentJson,
        diffJson: '', draftBy: 'admin', approveBy: 'admin', publishTime: new Date().toISOString(), remark: '回滚'
      })
    }
    saveDb(db)
    return ok(config, { ...v, effectMode: action === 'publish' ? 'RESTART_REQUIRED' : undefined })
  }

  // ---- 日志与调试 ----
  if (url === '/tran-logs' && method === 'GET') return ok(config, pageRows(db.tranLogs, params))
  const logDetail = url.match(/^\/tran-logs\/(.+)$/)
  if (logDetail) {
    const row = db.tranLogs.find((x: any) => x.tranflag === logDetail[1])
    return ok(config, {
      ...row,
      request: SAMPLE,
      response: '{"response":{"returnCode":"000000","returnMess":"成功"}}',
      stages: [
        { name: '原始Map', content: JSON.parse(SAMPLE) },
        { name: 'customer_req_map', content: JSON.parse(SAMPLE) },
        { name: 'provider_req_map', content: JSON.parse(SAMPLE) },
        { name: '目标报文', content: SAMPLE }
      ]
    })
  }
  if (url === '/debug/invoke' && method === 'POST') {
    let parsed: any
    try { parsed = JSON.parse(data.data || SAMPLE) } catch { parsed = { error: '报文解析失败' } }
    return ok(config, {
      stages: [
        { name: '原始Map', content: parsed },
        { name: 'customer_req_map', content: parsed },
        { name: 'provider_req_map', content: parsed },
        { name: '目标报文', content: JSON.stringify(parsed) },
        { name: 'provider_res_map', content: { response: { retCd: '000000', retMsg: '成功' } } },
        { name: 'customer_res_map', content: { response: { returnCode: '000000', returnMess: '成功' } } }
      ],
      response: '{"response":{"returnCode":"000000","returnMess":"成功"}}',
      costMs: 35, errorCode: null
    })
  }

  // ---- 接口模拟器（Mock） ----
  if (url === '/simulator/http' && method === 'POST') {
    let echo: any
    try { echo = JSON.parse(data.data || '{}') } catch { echo = { raw: data.data } }
    const body = JSON.stringify({
      code: '0000', message: '成功（Mock 响应）', timefly: '36',
      data: { echo, tranNo: echo?.tranNo || '', tip: '真实模式下由 accept-http 返回' }
    })
    return ok(config, { success: true, status: 200, body, headers: { 'Content-Type': 'application/json;charset=UTF-8' }, costMs: 36, error: null })
  }
  if (url === '/simulator/tcp' && method === 'POST') {
    const received = (data.data || '').length
    const inner = JSON.stringify({ code: '0000', message: '成功（Mock 响应）', data: '' })
    const body = String(inner.length).padStart(6, '0') + inner
    return ok(config, {
      success: true, status: 200, body,
      headers: { frame: '[6位长度][报文]', sentBytes: String(received), receivedBytes: String(body.length) },
      costMs: 18, error: null
    })
  }

  // ---- 运维 ----
  if (url === '/cache/status') {
    return ok(config, { cacheName: 'map', effectMode: 'RESTART_REQUIRED', refreshEndpoints: [], hashes: [
      { name: 'cusinfo', count: db.customerTransactions.length },
      { name: 'proinfo', count: db.providerTransactions.length },
      { name: 'cusreq', count: db.tags.filter((t: any) => t.ownerType === 'CUSTOMER_REQUEST').length },
      { name: 'proreq', count: db.tags.filter((t: any) => t.ownerType === 'PROVIDER_REQUEST').length }
    ] })
  }
  if (url === '/cache/refresh') return ok(config, { refreshed: false, effectMode: 'RESTART_REQUIRED', message: '当前为方案 A，请重启 ESB 进程使配置生效' })
  if (url === '/log-switches') return ok(config, db.logSwitches)
  const logSwitch = url.match(/^\/log-switches\/(.+)$/)
  if (logSwitch && method === 'PUT') {
    const row = db.logSwitches.find((x: any) => x.logType === logSwitch[1])
    if (row) row.enabled = data.enabled
    saveDb(db); return ok(config, row)
  }

  // ---- 通用 CRUD ----
  const parts = url.split('/').filter(Boolean)
  const collection = resources[parts[0]]
  if (collection) {
    const rows: any[] = db[collection]
    if (method === 'GET' && parts.length === 1) return ok(config, pageRows(rows, params))
    if (method === 'POST' && parts.length === 1) {
      const row = { ...data, id: nextId(rows) }
      rows.unshift(row); audit(db, collection, '新增', String(row.id), null, row); saveDb(db); return ok(config, row)
    }
    if (method === 'PUT' && parts.length === 2) {
      const row = rows.find((x) => x.id === Number(parts[1]))
      if (row) { const before = { ...row }; Object.assign(row, data, { id: row.id }); audit(db, collection, '修改', String(row.id), before, row) }
      saveDb(db); return ok(config, row)
    }
    if (method === 'DELETE' && parts.length === 2) {
      const idx = rows.findIndex((x) => x.id === Number(parts[1]))
      if (idx >= 0) { const removed = rows.splice(idx, 1)[0]; audit(db, collection, '删除', String(removed.id), removed, null) }
      saveDb(db); return ok(config, true)
    }
    if (method === 'PATCH' && parts.length === 3 && parts[2] === 'status') {
      const row = rows.find((x) => x.id === Number(parts[1]))
      if (row) {
        const field = data.statusKey || 'status'
        row[field] = data.status
      }
      saveDb(db); return ok(config, row)
    }
  }

  return ok(config, null)
}

// 让 mock 也生成一棵可展示的树（供 TagConfig 使用）
export { buildTagTree }
