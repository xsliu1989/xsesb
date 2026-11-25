import request from './request'

export const authApi = {
  login: (data: any) => request.post('/auth/login', data),
  me: () => request.get('/auth/me'),
  permissions: () => request.get('/auth/permissions'),
  logout: () => request.post('/auth/logout')
}

export const dashboardApi = {
  overview: () => request.get('/dashboard/overview')
}

export const crudApi = (resource: string) => ({
  page: (params: any) => request.get(`/${resource}`, { params }),
  list: (params: any) => request.get(`/${resource}`, { params: { ...params, size: 10000 } }),
  save: (data: any) => data.id ? request.put(`/${resource}/${data.id}`, data) : request.post(`/${resource}`, data),
  remove: (id: number) => request.delete(`/${resource}/${id}`),
  status: (id: number, status: string, statusKey?: string) => request.patch(`/${resource}/${id}/status`, { status, statusKey })
})

export const channelsApi = crudApi('channels')
export const customerTranApi = crudApi('customer-transactions')
export const providerTranApi = crudApi('provider-transactions')
export const sampleApi = crudApi('samples')
export const errorCodeApi = crudApi('error-codes')
export const cportApi = crudApi('cport')
export const keyApi = crudApi('keys')
export const userApi = crudApi('users')
export const roleApi = crudApi('roles')
export const auditApi = crudApi('audit-logs')

export const tagApi = {
  list: (params: any) => request.get('/tags', { params }),
  batch: (data: any) => request.put('/tags/batch', data),
  validate: (data: any) => request.post('/tags/validate', data),
  infer: (data: any) => request.post('/tags/infer', data),
  copy: (data: any) => request.post('/tags/copy', data)
}

export const mappingApi = {
  compare: (tranNo: string) => request.get('/mapping/compare', { params: { tranNo } })
}

export const versionApi = {
  page: (params: any) => request.get('/versions', { params }),
  draft: (data: any) => request.post('/versions/draft', data),
  diff: (id: number) => request.post(`/versions/${id}/diff`),
  approve: (id: number) => request.post(`/versions/${id}/approve`),
  publish: (id: number) => request.post(`/versions/${id}/publish`),
  rollback: (id: number) => request.post(`/versions/${id}/rollback`)
}

export const logApi = {
  page: (params: any) => request.get('/tran-logs', { params }),
  detail: (serid: string) => request.get(`/tran-logs/${serid}`)
}

export const debugApi = {
  invoke: (data: any) => request.post('/debug/invoke', data)
}

export const simulatorApi = {
  http: (data: any) => request.post('/simulator/http', data),
  tcp: (data: any) => request.post('/simulator/tcp', data)
}

export const systemApi = {
  cacheStatus: () => request.get('/cache/status'),
  refresh: () => request.post('/cache/refresh'),
  logSwitches: () => request.get('/log-switches'),
  saveLogSwitch: (data: any) => request.put(`/log-switches/${data.logType}`, data)
}
