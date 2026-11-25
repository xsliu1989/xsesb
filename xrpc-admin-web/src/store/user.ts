import { defineStore } from 'pinia'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('xrpc-admin-token') || '',
    username: '',
    realName: '',
    permissions: [] as string[]
  }),
  actions: {
    async login(data: any) {
      const res: any = await authApi.login(data)
      this.token = res.token
      this.username = res.username
      this.realName = res.realName || res.username
      localStorage.setItem('xrpc-admin-token', res.token)
    },
    async loadProfile() {
      if (!this.token) return
      const me: any = await authApi.me()
      this.username = me.username
      this.realName = me.realName || me.username
      this.permissions = (await authApi.permissions() as string[]) || []
    },
    logout() {
      this.token = ''
      this.username = ''
      this.permissions = []
      localStorage.removeItem('xrpc-admin-token')
    },
    has(perm: string) {
      return !perm || this.permissions.includes('*') || this.permissions.includes(perm)
    }
  }
})
