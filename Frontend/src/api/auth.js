const BASE = ''

async function request(url, options = {}) {
  const res = await fetch(BASE + url, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  return res.json()
}

export function login(username, password) {
  return request('/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  })
}

export function register(username, password) {
  return request('/register', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  })
}
