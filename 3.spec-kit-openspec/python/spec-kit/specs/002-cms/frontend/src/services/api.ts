const API_BASE = '/api';

export interface Category {
  id: number;
  name: string;
  created_at: string;
  updated_at: string;
}

export interface Content {
  id: number;
  title: string;
  category_id: number;
  category_name?: string;
  markdown_content: string;
  html_content: string;
  status: 'published' | 'draft';
  created_at: string;
  published_at: string | null;
}

export const api = {
  categories: {
    list: async (): Promise<Category[]> => {
      const res = await fetch(`${API_BASE}/categories`);
      const data = await res.json();
      return data.categories;
    },
    get: async (id: number): Promise<Category> => {
      const res = await fetch(`${API_BASE}/categories/${id}`);
      return res.json();
    },
    create: async (name: string): Promise<Category> => {
      const res = await fetch(`${API_BASE}/categories`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name }),
      });
      return res.json();
    },
    update: async (id: number, name: string): Promise<Category> => {
      const res = await fetch(`${API_BASE}/categories/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name }),
      });
      return res.json();
    },
    delete: async (id: number): Promise<void> => {
      await fetch(`${API_BASE}/categories/${id}`, { method: 'DELETE' });
    },
  },

  contents: {
    list: async (page = 1, limit = 10): Promise<{ contents: Content[]; total: number }> => {
      const res = await fetch(`${API_BASE}/contents/all?page=${page}&limit=${limit}`);
      return res.json();
    },
    listAll: async (page = 1, limit = 10, status?: string): Promise<{ contents: Content[]; total: number }> => {
      let url = `${API_BASE}/contents/admin?page=${page}&limit=${limit}`;
      if (status) url += `&status=${status}`;
      const res = await fetch(url);
      return res.json();
    },
    get: async (id: number): Promise<Content> => {
      const res = await fetch(`${API_BASE}/contents/${id}`);
      if (!res.ok) throw new Error('Content not found');
      return res.json();
    },
    create: async (data: { title: string; category_id: number; markdown_content: string; status: string }): Promise<Content> => {
      const res = await fetch(`${API_BASE}/contents`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });
      return res.json();
    },
    update: async (id: number, data: Partial<{ title: string; category_id: number; markdown_content: string; status: string }>): Promise<Content> => {
      const res = await fetch(`${API_BASE}/contents/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });
      return res.json();
    },
    delete: async (id: number): Promise<void> => {
      await fetch(`${API_BASE}/contents/${id}`, { method: 'DELETE' });
    },
    upload: async (file: File): Promise<{ content: string }> => {
      const formData = new FormData();
      formData.append('file', file);
      const res = await fetch(`${API_BASE}/contents/upload`, {
        method: 'POST',
        body: formData,
      });
      return res.json();
    },
  },
};