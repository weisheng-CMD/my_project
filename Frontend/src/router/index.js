import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import UserDashboard from '../views/UserDashboard.vue'
import AgentDashboard from '../views/AgentDashboard.vue'
import AdminDashboard from '../views/AdminDashboard.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/user', name: 'UserDashboard', component: UserDashboard, meta: { role: 'user' } },
  { path: '/agent', name: 'AgentDashboard', component: AgentDashboard, meta: { role: 'agent' } },
  { path: '/admin', name: 'AdminDashboard', component: AdminDashboard, meta: { role: 'admin' } },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：未登录跳转登录页，角色不对跳回自己的页面
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.path === '/login') {
    // 已登录用户访问登录页 → 直接跳转对应角色页
    if (token && role) {
      return next(`/${role}`)
    }
    return next()
  }

  // 未登录访问受保护页面 → 跳登录
  if (!token) {
    return next('/login')
  }

  // 角色不匹配 → 跳回自己角色的页面
  if (to.meta.role && to.meta.role !== role) {
    return next(`/${role}`)
  }

  next()
})

export default router
