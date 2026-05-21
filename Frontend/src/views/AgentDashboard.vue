<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../stores/auth.js'
import { listTickets, createTicket, getTicket, updateStatus } from '../api/ticket.js'

const router = useRouter()
const auth = useAuth()

const tickets = ref([])
const filterStatus = ref('')
const loading = ref(false)

const showCreate = ref(false)
const showDetail = ref(false)
const detailTicket = ref(null)

// 创建工单表单
const form = ref({ title: '', description: '', priority: '中', category: '' })

// 更新状态弹窗
const statusDialog = ref(false)
const statusTarget = ref(null)
const newStatus = ref('处理中')

async function loadTickets() {
  loading.value = true
  const res = await listTickets(filterStatus.value)
  tickets.value = res.code === 200 ? res.data : []
  loading.value = false
}

// ---- 创建工单 ----
async function handleCreate() {
  const res = await createTicket({ ...form.value, creatorId: Number(auth.state.userId) })
  if (res.code === 200) {
    showCreate.value = false
    form.value = { title: '', description: '', priority: '中', category: '' }
    loadTickets()
  } else {
    alert(res.message)
  }
}

// ---- 查看详情 ----
async function handleView(id) {
  const res = await getTicket(id)
  if (res.code === 200) {
    detailTicket.value = res.data
    showDetail.value = true
  }
}

// ---- 更新状态 ----
function openStatusDialog(ticket) {
  statusTarget.value = ticket
  newStatus.value = ticket.status === '待处理' ? '处理中' : ticket.status
  statusDialog.value = true
}
async function handleUpdateStatus() {
  const res = await updateStatus(statusTarget.value.id, newStatus.value)
  if (res.code === 200) {
    statusDialog.value = false
    loadTickets()
  } else alert(res.message)
}

function handleLogout() {
  auth.logout()
  router.push('/login')
}

const statusOptions = ['待处理', '处理中', '已解决', '已关闭']
const priorityOptions = ['低', '中', '高', '紧急']

function badgeClass(s) {
  if (s === '待处理') return 'badge-pending'
  if (s === '处理中') return 'badge-processing'
  if (s === '已解决') return 'badge-resolved'
  if (s === '已关闭') return 'badge-closed'
  return ''
}

onMounted(loadTickets)
</script>

<template>
  <div class="dashboard">
    <header class="topbar">
      <h1>客服工作台</h1>
      <div class="topbar-right">
        <span class="user-tag">{{ auth.state.username }}（客服）</span>
        <select v-model="filterStatus" @change="loadTickets">
          <option value="">全部状态</option>
          <option v-for="s in statusOptions" :key="s" :value="s">{{ s }}</option>
        </select>
        <button class="btn btn-primary" @click="showCreate = true">+ 新建工单</button>
        <button class="btn btn-outline" @click="handleLogout">退出</button>
      </div>
    </header>

    <main class="main">
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>状态</th>
            <th>优先级</th>
            <th>分类</th>
            <th>创建人ID</th>
            <th>处理人ID</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="tickets.length === 0">
            <td colspan="9" class="empty">暂无工单数据</td>
          </tr>
          <tr v-for="t in tickets" :key="t.id">
            <td>{{ t.id }}</td>
            <td class="title-cell">{{ t.title }}</td>
            <td><span :class="['badge', badgeClass(t.status)]">{{ t.status }}</span></td>
            <td>{{ t.priority }}</td>
            <td>{{ t.category || '-' }}</td>
            <td>{{ t.creatorId || '-' }}</td>
            <td>{{ t.assigneeId || '未分配' }}</td>
            <td class="time-cell">{{ t.createTime?.replace('T', ' ') || '-' }}</td>
            <td class="action-cell">
              <button class="btn btn-sm btn-outline" @click="handleView(t.id)">详情</button>
              <button class="btn btn-sm btn-outline" @click="openStatusDialog(t)">状态</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>

    <!-- ===== 新建工单弹窗 ===== -->
    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal">
        <h2>新建工单</h2>
        <div class="form-group">
          <label>标题 <span class="required">*</span></label>
          <input v-model="form.title" placeholder="工单标题" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea v-model="form.description" placeholder="工单描述" rows="3"></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>优先级</label>
            <select v-model="form.priority">
              <option v-for="p in priorityOptions" :key="p" :value="p">{{ p }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>分类</label>
            <input v-model="form.category" placeholder="如：账号问题" />
          </div>
        </div>
        <div class="modal-btns">
          <button class="btn btn-outline" @click="showCreate = false">取消</button>
          <button class="btn btn-primary" @click="handleCreate" :disabled="!form.title">确认创建</button>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetail && detailTicket" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal">
        <h2>工单详情 #{{ detailTicket.id }}</h2>
        <div class="detail-grid">
          <div><b>标题：</b>{{ detailTicket.title }}</div>
          <div><b>状态：</b><span :class="['badge', badgeClass(detailTicket.status)]">{{ detailTicket.status }}</span></div>
          <div><b>优先级：</b>{{ detailTicket.priority }}</div>
          <div><b>分类：</b>{{ detailTicket.category || '-' }}</div>
          <div><b>创建人ID：</b>{{ detailTicket.creatorId || '-' }}</div>
          <div><b>处理人ID：</b>{{ detailTicket.assigneeId || '未分配' }}</div>
          <div><b>创建时间：</b>{{ detailTicket.createTime?.replace('T', ' ') || '-' }}</div>
          <div><b>更新时间：</b>{{ detailTicket.updateTime?.replace('T', ' ') || '-' }}</div>
        </div>
        <div class="detail-desc">
          <b>描述：</b>
          <p>{{ detailTicket.description || '无' }}</p>
        </div>
        <div class="modal-btns">
          <button class="btn btn-outline" @click="showDetail = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 更新状态弹窗 -->
    <div v-if="statusDialog" class="modal-overlay" @click.self="statusDialog = false">
      <div class="modal modal-sm">
        <h2>更新状态 — #{{ statusTarget.id }}</h2>
        <p style="color:#64748b;margin-bottom:12px;">当前：<span :class="['badge', badgeClass(statusTarget.status)]">{{ statusTarget.status }}</span></p>
        <div class="form-group">
          <label>新状态</label>
          <select v-model="newStatus">
            <option v-for="s in statusOptions" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>
        <div class="modal-btns">
          <button class="btn btn-outline" @click="statusDialog = false">取消</button>
          <button class="btn btn-primary" @click="handleUpdateStatus">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #f1f5f9; color: #1e293b; }

.dashboard { min-height: 100vh; }

.topbar {
  display: flex; justify-content: space-between; align-items: center;
  background: #fff; padding: 14px 28px; border-bottom: 1px solid #e2e8f0;
}
.topbar h1 { font-size: 20px; }
.topbar-right { display: flex; gap: 10px; align-items: center; }
.topbar-right select { padding: 8px 12px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 14px; }
.user-tag { font-size: 13px; color: #2b7a4b; background: #dcfce7; padding: 2px 10px; border-radius: 99px; }

.main { padding: 24px 28px; }
.table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.table th, .table td { padding: 12px 16px; text-align: left; font-size: 14px; border-bottom: 1px solid #e2e8f0; }
.table th { background: #f8fafc; font-weight: 600; color: #64748b; font-size: 13px; white-space: nowrap; }
.table tbody tr:hover { background: #f8fafc; }
.title-cell { max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.time-cell { white-space: nowrap; font-size: 13px; color: #64748b; }
.action-cell { white-space: nowrap; }
.action-cell .btn { margin-right: 4px; }
.empty { text-align: center; color: #64748b; padding: 32px; }

.badge { display: inline-block; padding: 2px 10px; font-size: 12px; font-weight: 600; border-radius: 99px; }
.badge-pending  { background: #fef3c7; color: #ea580c; }
.badge-processing { background: #dbeafe; color: #2563eb; }
.badge-resolved { background: #dcfce7; color: #16a34a; }
.badge-closed   { background: #f1f5f9; color: #64748b; }

.btn { display: inline-flex; align-items: center; gap: 4px; padding: 8px 20px; border: none; border-radius: 6px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.15s; }
.btn-primary { background: #4f46e5; color: #fff; }
.btn-primary:hover { background: #4338ca; }
.btn-outline { background: transparent; border: 1px solid #e2e8f0; color: #1e293b; }
.btn-outline:hover { background: #f1f5f9; }
.btn-sm { padding: 4px 12px; font-size: 12px; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.35); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal { background: #fff; border-radius: 12px; padding: 28px; width: 520px; max-height: 85vh; overflow-y: auto; box-shadow: 0 10px 40px rgba(0,0,0,0.12); }
.modal-sm { width: 400px; }
.modal h2 { font-size: 18px; margin-bottom: 20px; }
.form-group { margin-bottom: 14px; }
.form-group label { display: block; font-size: 13px; font-weight: 600; color: #64748b; margin-bottom: 4px; }
.form-group input, .form-group select, .form-group textarea {
  width: 100%; padding: 8px 12px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 14px; outline: none;
}
.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
  border-color: #4f46e5; box-shadow: 0 0 0 3px rgba(79,70,229,0.08);
}
.form-row { display: flex; gap: 12px; }
.modal-btns { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 14px; }
.detail-desc { margin-top: 14px; font-size: 14px; }
.detail-desc p { margin-top: 4px; color: #64748b; white-space: pre-wrap; }
.loading { text-align: center; padding: 60px; color: #64748b; font-size: 16px; }
</style>
