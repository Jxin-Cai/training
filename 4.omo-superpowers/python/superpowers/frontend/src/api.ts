const API_BASE = '/api'

interface Category {
  id: number
  name: string
  slug: string
  sort_order: number
}

interface Content {
  id: number
  title: string
  slug: string
  category_id: number | null
  markdown_content: string | null
  html_content: string | null
  status: string
  author_id: number | null
  created_at: string
  updated_at: string
  published_at: string | null
}

interface User {
  id: number
  username: string
  role: string
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('token')
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options?.headers,
  }
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    const error = await response.json().catch(() => ({ detail: 'Request failed' }))
    throw new Error(error.detail)
  }
  return response.json()
}

export const api = {
  login: (username: string, password: string): Promise<{ access_token: string }> =>
    request<{ access_token: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
  
  register: (username: string, password: string): Promise<User> =>
    request<User>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
  
  getCurrentUser: (): Promise<User> => request<User>('/auth/me'),
  
  getCategories: (): Promise<Category[]> => request<Category[]>('/admin/categories'),
  createCategory: (data: { name: string; slug: string; sort_order: number }): Promise<Category> =>
    request<Category>('/admin/categories', { method: 'POST', body: JSON.stringify(data) }),
  updateCategory: (id: number, data: { name: string; slug: string; sort_order: number }): Promise<Category> =>
    request<Category>(`/admin/categories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteCategory: (id: number): Promise<{ message: string }> =>
    request<{ message: string }>(`/admin/categories/${id}`, { method: 'DELETE' }),
  
  getContents: (status?: string): Promise<Content[]> => 
    request<Content[]>(`/admin/contents${status ? `?status=${status}` : ''}`),
  getContent: (id: number): Promise<Content> => request<Content>(`/admin/contents/${id}`),
  createContent: (data: { title: string; slug: string; category_id: number; markdown_content: string }): Promise<Content> =>
    request<Content>('/admin/contents', { method: 'POST', body: JSON.stringify(data) }),
  updateContent: (id: number, data: Partial<{ title: string; slug: string; category_id: number; markdown_content: string }>): Promise<Content> =>
    request<Content>(`/admin/contents/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteContent: (id: number): Promise<{ message: string }> => 
    request<{ message: string }>(`/admin/contents/${id}`, { method: 'DELETE' }),
  publishContent: (id: number): Promise<{ message: string }> => 
    request<{ message: string }>(`/admin/contents/${id}/publish`, { method: 'POST' }),
  unpublishContent: (id: number): Promise<{ message: string }> => 
    request<{ message: string }>(`/admin/contents/${id}/unpublish`, { method: 'POST' }),
  
  getPublishedContents: (): Promise<Content[]> => request<Content[]>('/contents'),
  getContentBySlug: (slug: string): Promise<Content> => request<Content>(`/contents/${slug}`),
  getPublicCategories: (): Promise<Category[]> => request<Category[]>('/categories'),
}