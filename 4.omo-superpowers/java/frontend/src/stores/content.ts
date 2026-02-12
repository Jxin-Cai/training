import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Content, CreateContentRequest, UpdateContentRequest } from '@/types'
import { contentApi } from '@/api'

export const useContentStore = defineStore('content', () => {
  const contents = ref<Content[]>([])
  const publishedContents = ref<Content[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchContents() {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.getAll()
      contents.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch contents'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchPublishedContents() {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.getPublished()
      publishedContents.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch published contents'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchContentById(id: string): Promise<Content | null> {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.getById(id)
      return response.data
    } catch (e) {
      error.value = 'Failed to fetch content'
      console.error(e)
      return null
    } finally {
      loading.value = false
    }
  }

  async function createContent(data: CreateContentRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.create(data)
      contents.value.push(response.data)
      return response.data
    } catch (e) {
      error.value = 'Failed to create content'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateContent(id: string, data: UpdateContentRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.update(id, data)
      const index = contents.value.findIndex(c => c.id === id)
      if (index !== -1) {
        contents.value[index] = response.data
      }
      return response.data
    } catch (e) {
      error.value = 'Failed to update content'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function publishContent(id: string) {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.publish(id)
      const index = contents.value.findIndex(c => c.id === id)
      if (index !== -1) {
        contents.value[index] = response.data
      }
      return response.data
    } catch (e) {
      error.value = 'Failed to publish content'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function unpublishContent(id: string) {
    loading.value = true
    error.value = null
    try {
      const response = await contentApi.unpublish(id)
      const index = contents.value.findIndex(c => c.id === id)
      if (index !== -1) {
        contents.value[index] = response.data
      }
      return response.data
    } catch (e) {
      error.value = 'Failed to unpublish content'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteContent(id: string) {
    loading.value = true
    error.value = null
    try {
      await contentApi.delete(id)
      contents.value = contents.value.filter(c => c.id !== id)
    } catch (e) {
      error.value = 'Failed to delete content'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    contents,
    publishedContents,
    loading,
    error,
    fetchContents,
    fetchPublishedContents,
    fetchContentById,
    createContent,
    updateContent,
    publishContent,
    unpublishContent,
    deleteContent,
  }
})
