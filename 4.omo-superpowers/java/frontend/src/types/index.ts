export interface Category {
  id: string
  name: string
  description: string
  parentId: string | null
  level: number
  path: string
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface CategoryTreeNode {
  id: string
  name: string
  description: string
  parentId: string | null
  level: number
  sortOrder: number
  createdAt: string
  updatedAt: string
  children: CategoryTreeNode[]
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
  parentId?: string | null
}

export interface UpdateCategoryRequest {
  name?: string
  description?: string
  parentId?: string | null
  sortOrder?: number
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

export interface ImageUploadResponse {
  url: string
}
