import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  { path: '/login', component: () => import('@/views/login/index.vue'), meta: { public: true } },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '工作台', icon: 'DataBoard' } },
      { path: 'config/channels', component: () => import('@/views/config/ChannelList.vue'), meta: { title: '渠道系统信息', group: '配置中心' } },
      { path: 'config/customer-transactions', component: () => import('@/views/config/CustomerTransactionList.vue'), meta: { title: '渠道接口', group: '配置中心' } },
      { path: 'config/provider-transactions', component: () => import('@/views/config/ProviderTransactionList.vue'), meta: { title: '数据源系统信息', group: '配置中心' } },
      { path: 'config/tags', component: () => import('@/views/config/TagConfig.vue'), meta: { title: 'Tag 配置', group: '配置中心' } },
      { path: 'config/mapping', component: () => import('@/views/config/MappingView.vue'), meta: { title: '字段映射', group: '配置中心' } },
      { path: 'config/samples', component: () => import('@/views/config/SampleList.vue'), meta: { title: '报文样例', group: '配置中心' } },
      { path: 'config/versions', component: () => import('@/views/config/VersionList.vue'), meta: { title: '发布管理', group: '配置中心' } },
      { path: 'monitor/logs', component: () => import('@/views/monitor/TranLogList.vue'), meta: { title: '交易日志', group: '运行监控' } },
      { path: 'monitor/debug', component: () => import('@/views/monitor/DebugPanel.vue'), meta: { title: '在线调试', group: '运行监控' } },
      { path: 'monitor/cache', component: () => import('@/views/monitor/CacheStatus.vue'), meta: { title: '缓存状态', group: '运行监控' } },
      { path: 'system/error-codes', component: () => import('@/views/system/ErrorCodeList.vue'), meta: { title: '错误码字典', group: '系统管理' } },
      { path: 'system/keys', component: () => import('@/views/system/KeyList.vue'), meta: { title: '密钥管理', group: '系统管理' } },
      { path: 'system/cport', component: () => import('@/views/system/CPortConfig.vue'), meta: { title: 'TCP 端口映射', group: '系统管理' } },
      { path: 'system/log-switches', component: () => import('@/views/system/LogSwitch.vue'), meta: { title: '日志开关', group: '系统管理' } },
      { path: 'system/users', component: () => import('@/views/system/UserList.vue'), meta: { title: '用户管理', group: '系统管理' } },
      { path: 'system/roles', component: () => import('@/views/system/RoleList.vue'), meta: { title: '角色权限', group: '系统管理' } },
      { path: 'system/audit', component: () => import('@/views/system/AuditLog.vue'), meta: { title: '操作审计', group: '系统管理' } },
      { path: 'tools/simulator', component: () => import('@/views/tools/Simulator.vue'), meta: { title: '接口模拟器', group: '开发工具' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const user = useUserStore()
  if (!to.meta.public && !user.token) return { path: '/login', query: { redirect: to.fullPath } }
  return true
})

export default router
