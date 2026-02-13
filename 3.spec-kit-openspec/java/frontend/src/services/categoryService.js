import api from './api'

export default {
  async getAll() {
    return await api.get('/categories')
  },

  async getTree() {
    return await api.get('/categories/tree')
  },

  async getById(id) {
    return await api.get(`/categories/${id}`)
  },

  async getChildren(id) {
    return await api.get(`/categories/${id}/children`)
  },

  async getDescendants(id) {
    return await api.get(`/categories/${id}/descendants`)
  },

  async create(data) {
    return await api.post('/categories', data)
  },

  async update(id, data) {
    return await api.put(`/categories/${id}`, data)
  },

  async delete(id, handleChildren = 'PREVENT', handleContent = 'PREVENT') {
    return await api.delete(`/categories/${id}?handleChildren=${handleChildren}&handleContent=${handleContent}`)
  }
}
