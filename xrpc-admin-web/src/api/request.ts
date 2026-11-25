import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/store/user'
import { mockAdapter } from '@/mock/mockAdapter'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/admin-api',
  timeout: 15000
})

request.interceptors.request.use((cfg) => {
  const token = useUserStore().token
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

request.interceptors.response.use(
  (res) => {
    const body: any = res.data
    if (body && body.code !== undefined && body.code !== 0) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(body)
    }
    return body && body.data !== undefined ? body.data : body
  },
  (err) => {
    if (err.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

if (USE_MOCK) {
  // 同步设置适配器，避免首屏请求早于 Mock 初始化
  request.defaults.adapter = mockAdapter as AxiosRequestConfig['adapter']
}

// 响应拦截器已经解包 R.data，这里把类型收敛为 any，避免调用方误认为拿到 AxiosResponse
export default request as any
