import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'
import { categoryApi } from '@/api'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getAll()
      categories.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch categories'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function createCategory(data: CreateCategoryRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.create(data)
      categories.value.push(response.data)
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
      const index = categories.value.findIndex(c => c.id === id)
      if (index !== -1) {
        categories.value[index] = response.data
      }
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
      categories.value = categories.value.filter(c => c.id !== id)
    } catch (e) {
      error.value = 'Failed to delete category'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    categories,
    loading,
    error,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
  }
})
