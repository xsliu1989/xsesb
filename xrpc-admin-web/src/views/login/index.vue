<template>
  <div class="login-page">
    <div class="login-box">
      <h1>ESB 管理控制台</h1>
      <p class="text-muted">配置 Tag 源数据 / 目标数据、渠道与数据源接口</p>
      <el-form :model="form" label-width="70px" @keyup.enter="submit">
        <el-form-item label="账号"><el-input v-model="form.username" placeholder="admin" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" placeholder="admin123" /></el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <p class="text-muted" style="margin-top: 16px">默认账号：admin / admin123</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const form = reactive({ username: 'admin', password: 'admin123' })
const loading = ref(false)
const user = useUserStore()
const router = useRouter()
const route = useRoute()

async function submit() {
  loading.value = true
  try {
    await user.login({ ...form })
    await user.loadProfile()
    router.push((route.query.redirect as string) || '/dashboard')
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally { loading.value = false }
}
</script>

<style scoped>
.login-page { height: 100%; display: flex; align-items: center; justify-content: center; background: #1d2b3a; }
.login-box { width: 360px; background: #fff; border-radius: 6px; padding: 32px; box-shadow: 0 8px 30px rgba(0,0,0,.2); }
.login-box h1 { font-size: 20px; margin: 0 0 6px; text-align: center; }
.login-box p { text-align: center; }
</style>
