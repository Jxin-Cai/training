import api from './api'

export default {
  async upload(file, onProgress) {
    const formData = new FormData()
    formData.append('file', file)

    return await api.post('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(percentCompleted)
        }
      }
    })
  },

  async getById(id) {
    return await api.get(`/images/${id}`)
  },

  async delete(id) {
    return await api.delete(`/images/${id}`)
  }
}
