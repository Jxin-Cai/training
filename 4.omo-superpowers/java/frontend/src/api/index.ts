import axios from 'axios'
import type { Category, Content, CreateCategoryRequest, UpdateCategoryRequest, CreateContentRequest, UpdateContentRequest } from '@/types'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const categoryApi = {
  getAll: () => api.get<Category[]>('/categories'),
  getById: (id: string) => api.get<Category>(`/categories/${id}`),
  create: (data: CreateCategoryRequest) => api.post<Category>('/categories', data),
  update: (id: string, data: UpdateCategoryRequest) => api.put<Category>(`/categories/${id}`, data),
  delete: (id: string) => api.delete(`/categories/${id}`),
}

export const contentApi = {
  getAll: () => api.get<Content[]>('/contents'),
  getPublished: () => api.get<Content[]>('/contents/published'),
  getById: (id: string) => api.get<Content>(`/contents/${id}`),
  getByCategory: (categoryId: string) => api.get<Content[]>(`/contents/category/${categoryId}`),
  create: (data: CreateContentRequest) => api.post<Content>('/contents', data),
  update: (id: string, data: UpdateContentRequest) => api.put<Content>(`/contents/${id}`, data),
  publish: (id: string) => api.post<Content>(`/contents/${id}/publish`),
  unpublish: (id: string) => api.post<Content>(`/contents/${id}/unpublish`),
  delete: (id: string) => api.delete(`/contents/${id}`),
}

export default api
