const API_BASE = '/api'

function getToken(): string | null {
  return localStorage.getItem('token')
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers: {
      ...headers,
      ...options.headers,
    },
  })

  if (response.status === 401) {
    localStorage.removeItem('token')
    window.location.href = '/admin'
    throw new Error('Unauthorized')
  }

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

  // Categories (public)
  getCategories: () => request<Category[]>('/categories'),
  
  // Categories (admin)
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

  // Articles - all (admin)
  getArticles: () => request<Article[]>('/articles'),
  getArticle: (id: number) => request<Article>(`/articles/${id}`),
  
  // Articles - published (public)
  getPublishedArticles: () => request<Article[]>('/articles/published'),
  getPublishedArticle: (id: number) => request<Article>(`/articles/published/${id}`),

  // Article operations (admin)
  createArticle: (data: ArticleInput) =>
    request<Article>('/articles', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateArticle: (id: number, data: Partial<ArticleInput>) =>
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
  published_at: string | null
  created_at: string
  updated_at: string
}

// Backend will handle markdown -> HTML rendering
// Frontend only needs to send markdown content + metadata
export interface ArticleInput {
  title: string
  content_md: string
  category_id: number
  status: string
}