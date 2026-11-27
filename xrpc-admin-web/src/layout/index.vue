<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <div class="logo-mark">ESB</div>
        <span>管理控制台</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="$route.path"
          router
          background-color="#001529"
          text-color="#c0c4cc"
          active-text-color="#409eff"
          unique-opened
        >
          <template v-for="group in groupedMenus" :key="group.name">
            <el-sub-menu v-if="group.children.length > 1" :index="group.name">
              <template #title>{{ group.name }}</template>
              <el-menu-item v-for="m in group.children" :key="m.path" :index="m.path">{{ m.title }}</el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="group.children[0].path">{{ group.children[0].title }}</el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
          <span class="text-muted">{{ $route.meta.group || '首页' }} / </span>
          <strong>{{ $route.meta.title || '工作台' }}</strong>
        </div>
        <div class="header-right">
          <el-tag size="small" type="warning" effect="plain">方案 A：发布后需重启生效</el-tag>
          <el-dropdown @command="onCommand">
            <span class="user">
              <el-avatar :size="28">{{ (user.realName || 'U').slice(0, 1) }}</el-avatar>
              {{ user.realName || user.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api'

const router = useRouter()
const user = useUserStore()

const groupedMenus: { name: string; children: { path: string; title: string }[] }[] = [
  { name: '总览', children: [{ path: '/dashboard', title: '工作台' }] },
  { name: '配置中心', children: [
    { path: '/config/channels', title: '渠道系统信息' },
    { path: '/config/customer-transactions', title: '渠道接口' },
    { path: '/config/provider-transactions', title: '数据源系统信息' },
    { path: '/config/tags', title: 'Tag 配置' },
    { path: '/config/mapping', title: '字段映射' },
    { path: '/config/samples', title: '报文样例' },
    { path: '/config/versions', title: '发布管理' }
  ] },
  { name: '运行监控', children: [
    { path: '/monitor/logs', title: '交易日志' },
    { path: '/monitor/debug', title: '在线调试' },
    { path: '/monitor/cache', title: '缓存状态' }
  ] },
  { name: '系统管理', children: [
    { path: '/system/error-codes', title: '错误码字典' },
    { path: '/system/keys', title: '密钥管理' },
    { path: '/system/cport', title: 'TCP 端口映射' },
    { path: '/system/log-switches', title: '日志开关' },
    { path: '/system/users', title: '用户管理' },
    { path: '/system/roles', title: '角色权限' },
    { path: '/system/audit', title: '操作审计' }
  ] },
  { name: '开发工具', children: [
    { path: '/tools/simulator', title: '接口模拟器' }
  ] }
]

onMounted(() => { user.loadProfile().catch(() => {}) })

function onCommand(cmd: string) {
  if (cmd === 'logout') {
    authApi.logout().catch(() => {})
    user.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #001529; overflow: hidden; }
.logo { height: 60px; display: flex; align-items: center; gap: 10px; padding: 0 18px; color: #fff; font-size: 16px; font-weight: 600; }
.logo-mark { background: #409eff; border-radius: 4px; padding: 4px 6px; font-size: 12px; }
.header { background: #fff; border-bottom: 1px solid #e4e7ed; display: flex; align-items: center; justify-content: space-between; padding: 0 18px; }
.breadcrumb { font-size: 14px; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user { display: flex; align-items: center; gap: 6px; cursor: pointer; font-size: 13px; }
.main { padding: 0; background: #f5f7fa; overflow: auto; }
</style>
