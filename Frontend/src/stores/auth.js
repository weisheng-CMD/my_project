import { reactive, computed } from 'vue'

const state = reactive({
  token: localStorage.getItem('token') || '',
  userId: localStorage.getItem('userId') || '',
  username: localStorage.getItem('username') || '',
  role: localStorage.getItem('role') || ''
})

export function useAuth() {
  const isLoggedIn = computed(() => !!state.token)

  function login(data) {
    state.token = data.token
    state.userId = data.userId
    state.username = data.username
    state.role = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
  }

  function logout() {
    state.token = ''
    state.userId = ''
    state.username = ''
    state.role = ''
    localStorage.clear()
  }

  return { state, isLoggedIn, login, logout }
}
