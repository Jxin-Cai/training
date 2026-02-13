import api from './api'

export default {
  async getAll(params = {}) {
    const query = new URLSearchParams(params).toString()
    return await api.get(`/content?${query}`)
  },

  async getById(id) {
    return await api.get(`/content/${id}`)
  },

  async create(data) {
    return await api.post('/content', data)
  },

  async update(id, data) {
    return await api.put(`/content/${id}`, data)
  },

  async delete(id) {
    return await api.delete(`/content/${id}`)
  },

  async publish(id) {
    return await api.post(`/content/${id}/publish`)
  },

  async unpublish(id) {
    return await api.post(`/content/${id}/unpublish`)
  }
}
