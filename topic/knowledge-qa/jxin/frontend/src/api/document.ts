// rag_demo/frontend/src/api/document.ts
import request from './request'

export interface UploadResponse {
  doc_id: string
  filename: string
  chunks: number
}

export interface Document {
  id: string
  filename: string
  chunk_count: number
  created_at: string
}

export interface DocumentListResponse {
  documents: Document[]
}

export interface DeleteResponse {
  success: boolean
  message: string
}

export const documentApi = {
  upload: (file: File): Promise<UploadResponse> => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  list: (): Promise<DocumentListResponse> =>
    request.get('/documents'),

  delete: (docId: string): Promise<DeleteResponse> =>
    request.delete(`/documents/${docId}`),
}
