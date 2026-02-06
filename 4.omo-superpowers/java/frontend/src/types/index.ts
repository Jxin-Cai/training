export interface Category {
  id: string
  name: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface Content {
  id: string
  title: string
  markdownContent: string
  htmlContent: string
  categoryId: string
  categoryName: string | null
  status: 'DRAFT' | 'PUBLISHED'
  createdAt: string
  updatedAt: string
  publishedAt: string | null
}

export interface CreateCategoryRequest {
  name: string
  description: string
}

export interface UpdateCategoryRequest {
  name: string
  description: string
}

export interface CreateContentRequest {
  title: string
  markdownContent: string
  categoryId: string
}

export interface UpdateContentRequest {
  title: string
  markdownContent: string
  categoryId: string
}
