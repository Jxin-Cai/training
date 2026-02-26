const BASE = ''

async function request(method, path, body = null) {
  const opts = { method, headers: {} }
  if (body !== null) {
    opts.headers['Content-Type'] = 'application/json'
    opts.body = JSON.stringify(body)
  }
  const res = await fetch(BASE + path, opts)
  const text = await res.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch (_) {}
  if (!res.ok) {
    const err = new Error(data?.error || res.statusText || 'Request failed')
    err.status = res.status
    err.data = data
    throw err
  }
  return data
}

export const api = {
  categories: {
    list: () => request('GET', '/api/categories'),
    get: (id) => request('GET', `/api/categories/${id}`),
    create: (name, description = '') => request('POST', '/api/categories', { name, description }),
    update: (id, name, description = '') => request('PUT', `/api/categories/${id}`, { name, description }),
    delete: (id) => request('DELETE', `/api/categories/${id}`),
  },
  contents: {
    list: () => request('GET', '/api/contents'),
    get: (id) => request('GET', `/api/contents/${id}`),
    create: (payload) => request('POST', '/api/contents', payload),
    update: (id, payload) => request('PUT', `/api/contents/${id}`, payload),
    delete: (id) => request('DELETE', `/api/contents/${id}`),
    publish: (id) => request('POST', `/api/contents/${id}/publish`),
    unpublish: (id) => request('POST', `/api/contents/${id}/unpublish`),
  },
  uploadMarkdown: (payload) => request('POST', '/api/uploads/markdown', payload),
  public: {
    list: () => request('GET', '/api/public/contents'),
    get: (id) => request('GET', `/api/public/contents/${id}`),
  },
}
