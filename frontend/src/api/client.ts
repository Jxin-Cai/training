const API_BASE = '/api'

interface ApiResponse<T> {
  success: boolean
  data?: T
  error?: string
}

async function fetchApi<T>(endpoint: string, options?: RequestInit): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
      ...options,
    })
    const data = await response.json()
    return { success: response.ok, data, error: response.ok ? undefined : data.detail }
  } catch (error) {
    return { success: false, error: String(error) }
  }
}

export default fetchApi