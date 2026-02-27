import { createContext, useContext, useState, ReactNode } from 'react';
import { api, Content } from '../services/api';

interface ContentContextType {
  contents: Content[];
  total: number;
  loading: boolean;
  error: string | null;
  loadContents: (page?: number) => Promise<void>;
  loadAllContents: (page?: number, status?: string) => Promise<void>;
  getContent: (id: number) => Promise<Content>;
  createContent: (data: { title: string; category_id: number; markdown_content: string; status: string }) => Promise<Content>;
  updateContent: (id: number, data: Partial<{ title: string; category_id: number; markdown_content: string; status: string }>) => Promise<Content>;
  deleteContent: (id: number) => Promise<void>;
}

const ContentContext = createContext<ContentContextType | null>(null);

export function ContentProvider({ children }: { children: ReactNode }) {
  const [contents, setContents] = useState<Content[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadContents = async (page = 1) => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.contents.list(page);
      setContents(data.contents);
      setTotal(data.total);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load contents');
    } finally {
      setLoading(false);
    }
  };

  const loadAllContents = async (page = 1, status?: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.contents.listAll(page, 100, status);
      setContents(data.contents);
      setTotal(data.total);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load contents');
    } finally {
      setLoading(false);
    }
  };

  const getContent = async (id: number) => {
    return api.contents.get(id);
  };

  const createContent = async (data: { title: string; category_id: number; markdown_content: string; status: string }) => {
    return api.contents.create(data);
  };

  const updateContent = async (id: number, data: Partial<{ title: string; category_id: number; markdown_content: string; status: string }>) => {
    return api.contents.update(id, data);
  };

  const deleteContent = async (id: number) => {
    await api.contents.delete(id);
  };

  return (
    <ContentContext.Provider value={{ contents, total, loading, error, loadContents, loadAllContents, getContent, createContent, updateContent, deleteContent }}>
      {children}
    </ContentContext.Provider>
  );
}

export function useContents() {
  const ctx = useContext(ContentContext);
  if (!ctx) throw new Error('useContents must be used within ContentProvider');
  return ctx;
}