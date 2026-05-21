const BASE = '/tickets'

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request(url, options = {}) {
  const res = await fetch(BASE + url, {
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    ...options
  })
  return res.json()
}

/** 创建工单 */
export function createTicket(data) {
  return request('', { method: 'POST', body: JSON.stringify(data) })
}

/** 查看工单详情 */
export function getTicket(id) {
  return request(`/${id}`)
}

/** 按状态筛选工单列表 */
export function listTickets(status = '') {
  const query = status ? `?status=${encodeURIComponent(status)}` : ''
  return request(query)
}

/** 更新工单状态 */
export function updateStatus(id, status) {
  return request(`/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) })
}

/** 分配工单 */
export function assignTicket(id, assigneeId) {
  return request(`/${id}/assign`, { method: 'PUT', body: JSON.stringify({ assigneeId }) })
}

/** 删除工单（逻辑删除） */
export function deleteTicket(id) {
  return request(`/${id}`, { method: 'DELETE' })
}
