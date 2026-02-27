const API_BASE = '/api'

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })
  if (!response.ok) {
    const error = await response.json().catch(() => ({ detail: 'Request failed' }))
    throw new Error(error.detail)
  }
  return response.json()
}

export const api = {
  login: (username: string, password: string) =>
    request<{ access_token: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  getCategories: () => request<Category[]>('/categories'),
  createCategory: (name: string) =>
    request<Category>('/categories', {
      method: 'POST',
      body: JSON.stringify({ name }),
    }),
  updateCategory: (id: number, name: string) =>
    request<Category>(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ name }),
    }),
  deleteCategory: (id: number) =>
    request<void>(`/categories/${id}`, { method: 'DELETE' }),

  getArticles: () => request<Article[]>('/articles'),
  getPublishedArticles: () => request<Article[]>('/articles/published'),
  getArticle: (id: number) => request<Article>(`/articles/${id}`),
  createArticle: (data: ArticleInput) =>
    request<Article>('/articles', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateArticle: (id: number, data: ArticleInput) =>
    request<Article>(`/articles/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  deleteArticle: (id: number) =>
    request<void>(`/articles/${id}`, { method: 'DELETE' }),
}

export interface Category {
  id: number
  name: string
  created_at: string
}

export interface Article {
  id: number
  title: string
  content_md: string
  content_html: string
  category_id: number
  status: string
  created_at: string
  updated_at: string
}

export interface ArticleInput {
  title: string
  content_md: string
  content_html: string
  category_id: number
  status: string
}