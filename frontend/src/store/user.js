import { defineStore } from 'pinia'
import { login, register, getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLogin: (state) => !!state.token,
    username: (state) => state.userInfo?.nickname || state.userInfo?.username || '用户',
    isAdmin: (state) => state.userInfo?.role === 'ADMIN'
  },

  actions: {
    // 登录
    async loginAction(loginForm) {
      try {
        const res = await login(loginForm)
        this.token = res.data.token
        this.userInfo = res.data.user
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.user))
        ElMessage.success('登录成功')
        return true
      } catch (error) {
        return false
      }
    },

    // 注册
    async registerAction(registerForm) {
      try {
        await register(registerForm)
        ElMessage.success('注册成功，请登录')
        return true
      } catch (error) {
        return false
      }
    },

    // 获取用户信息
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        return res.data
      } catch (error) {
        return null
      }
    },

    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    // 退出登录
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
