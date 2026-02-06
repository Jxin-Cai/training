import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Category, CategoryTreeNode, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'
import { categoryApi } from '@/api'

export const useCategoryStore = defineStore('category', () => {
  const categoryTree = ref<CategoryTreeNode[]>([])
  const flatCategories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const categories = computed(() => flatCategories.value)

  async function fetchCategoryTree() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getTree()
      categoryTree.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch category tree'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchFlatCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getFlat()
      flatCategories.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch categories'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchCategories() {
    await Promise.all([fetchCategoryTree(), fetchFlatCategories()])
  }

  async function createCategory(data: CreateCategoryRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.create(data)
      await fetchCategories()
      return response.data
    } catch (e) {
      error.value = 'Failed to create category'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateCategory(id: string, data: UpdateCategoryRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.update(id, data)
      await fetchCategories()
      return response.data
    } catch (e) {
      error.value = 'Failed to update category'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteCategory(id: string) {
    loading.value = true
    error.value = null
    try {
      await categoryApi.delete(id)
      await fetchCategories()
    } catch (e: any) {
      if (e.response?.data?.error) {
        error.value = e.response.data.error
        alert(e.response.data.error)
      } else {
        error.value = 'Failed to delete category'
      }
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  function getCategoryPath(categoryId: string): string[] {
    const path: string[] = []
    let current = flatCategories.value.find(c => c.id === categoryId)
    while (current) {
      path.unshift(current.name)
      current = current.parentId 
        ? flatCategories.value.find(c => c.id === current!.parentId)
        : undefined
    }
    return path
  }

  return {
    categoryTree,
    flatCategories,
    categories,
    loading,
    error,
    fetchCategoryTree,
    fetchFlatCategories,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    getCategoryPath,
  }
})
