import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { api, Category } from '../services/api';

interface CategoryContextType {
  categories: Category[];
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  createCategory: (name: string) => Promise<void>;
  updateCategory: (id: number, name: string) => Promise<void>;
  deleteCategory: (id: number) => Promise<void>;
}

const CategoryContext = createContext<CategoryContextType | null>(null);

export function CategoryProvider({ children }: { children: ReactNode }) {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.categories.list();
      setCategories(data);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  const createCategory = async (name: string) => {
    await api.categories.create(name);
    await refresh();
  };

  const updateCategory = async (id: number, name: string) => {
    await api.categories.update(id, name);
    await refresh();
  };

  const deleteCategory = async (id: number) => {
    await api.categories.delete(id);
    await refresh();
  };

  useEffect(() => {
    refresh();
  }, []);

  return (
    <CategoryContext.Provider value={{ categories, loading, error, refresh, createCategory, updateCategory, deleteCategory }}>
      {children}
    </CategoryContext.Provider>
  );
}

export function useCategories() {
  const ctx = useContext(CategoryContext);
  if (!ctx) throw new Error('useCategories must be used within CategoryProvider');
  return ctx;
}