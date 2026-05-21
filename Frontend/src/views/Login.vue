<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi, register as registerApi } from '../api/auth.js'
import { useAuth } from '../stores/auth.js'

const router = useRouter()
const auth = useAuth()

const isRegister = ref(false)
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

function clearError() {
  error.value = ''
  success.value = ''
}

async function handleLogin() {
  clearError()
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  try {
    const res = await loginApi(username.value, password.value)
    if (res.code === 200) {
      auth.login(res.data)
      const role = res.data.role
      if (role === 'admin') router.push('/admin')
      else if (role === 'agent') router.push('/agent')
      else router.push('/user')
    } else {
      error.value = res.message || '登录失败'
    }
  } catch (e) {
    error.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  clearError()
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  if (password.value !== confirmPassword.value) {
    error.value = '两次密码不一致'
    return
  }
  loading.value = true
  try {
    const res = await registerApi(username.value, password.value)
    if (res.code === 200) {
      success.value = '注册成功！请登录'
      isRegister.value = false
      confirmPassword.value = ''
    } else {
      error.value = res.message || '注册失败'
    }
  } catch (e) {
    error.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  clearError()
  isRegister.value = !isRegister.value
  if (!isRegister.value) confirmPassword.value = ''
}

const testAccounts = [
  { label: '管理员 admin', role: 'admin', user: 'admin', pass: '123456', icon: '🛡️' },
  { label: '客服  agent', role: 'agent', user: 'agent', pass: '123456', icon: '🎧' },
  { label: '用户   user', role: 'user', user: 'user', pass: '123456', icon: '👤' }
]

function fillAccount(account) {
  clearError()
  username.value = account.user
  password.value = account.pass
  isRegister.value = false
}
</script>

<template>
  <div class="login-page">
    <!-- 左侧快捷账号 -->
    <div class="quick-sidebar">
      <div class="quick-header">测试账号</div>
      <div class="quick-hint">点击自动填入</div>
      <button
        v-for="acc in testAccounts"
        :key="acc.role"
        class="quick-btn"
        :class="'quick-' + acc.role"
        @click="fillAccount(acc)"
      >
        <span class="quick-icon">{{ acc.icon }}</span>
        <span class="quick-label">{{ acc.label }}</span>
      </button>
    </div>

    <!-- 右侧登录卡片 -->
    <div class="login-card">
      <h1>智能客服工单系统</h1>
      <p class="subtitle">{{ isRegister ? '注册新账号' : '请登录您的账号' }}</p>

      <div v-if="error" class="error-msg">{{ error }}</div>
      <div v-if="success" class="success-msg">{{ success }}</div>

      <div class="form-group">
        <label>用户名</label>
        <input v-model="username" type="text" placeholder="请输入用户名" @keyup.enter="isRegister ? handleRegister() : handleLogin()" />
      </div>

      <div class="form-group">
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入密码" @keyup.enter="isRegister ? handleRegister() : handleLogin()" />
      </div>

      <div v-if="isRegister" class="form-group">
        <label>确认密码</label>
        <input v-model="confirmPassword" type="password" placeholder="请再次输入密码" @keyup.enter="handleRegister" />
      </div>

      <button v-if="!isRegister" class="btn btn-primary btn-block" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登 录' }}
      </button>
      <button v-else class="btn btn-primary btn-block" @click="handleRegister" :disabled="loading">
        {{ loading ? '注册中...' : '注 册' }}
      </button>

      <div class="toggle">
        <span v-if="!isRegister">还没有账号？<a href="#" @click.prevent="toggleMode">立即注册</a></span>
        <span v-else>已有账号？<a href="#" @click.prevent="toggleMode">去登录</a></span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 28px;
  background: #f1f5f9;
}

/* 左侧快捷账号栏 */
.quick-sidebar {
  background: #fff;
  border-radius: 12px;
  padding: 24px 20px;
  width: 200px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.quick-header {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
}
.quick-hint {
  font-size: 12px;
  color: #94a3b8;
  text-align: center;
  margin-bottom: 4px;
}
.quick-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.15s;
}
.quick-btn:hover {
  transform: translateX(2px);
}
.quick-icon { font-size: 18px; }
.quick-label { font-weight: 500; color: #334155; }
.quick-admin:hover { border-color: #9333ea; background: #faf5ff; }
.quick-agent:hover { border-color: #16a34a; background: #f0fdf4; }
.quick-user:hover  { border-color: #2563eb; background: #eff6ff; }

/* 登录卡片 */
.login-card {
  background: #fff;
  padding: 40px 36px;
  border-radius: 12px;
  width: 400px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.login-card h1 {
  font-size: 22px;
  text-align: center;
  margin-bottom: 6px;
}
.subtitle {
  text-align: center;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 24px;
}
.error-msg {
  background: #fef2f2;
  color: #dc2626;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 16px;
}
.success-msg {
  background: #f0fdf4;
  color: #16a34a;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 16px;
}
.toggle {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: #64748b;
}
.toggle a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 600;
}
.toggle a:hover {
  text-decoration: underline;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 4px;
}
.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.15s;
}
.form-group input:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79,70,229,0.08);
}
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-primary { background: #4f46e5; color: #fff; }
.btn-primary:hover { background: #4338ca; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-block { width: 100%; }
</style>
