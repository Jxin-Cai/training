<template>
  <div class="frontend-layout">
    <article v-if="article" class="article-detail">
      <h1>{{ article.title }}</h1>
      <div class="article-meta">
        <span>{{ article.category }}</span>
        <span>{{ article.author }}</span>
        <span>{{ article.createdAt }}</span>
      </div>
      <div class="article-content" v-html="renderedContent"></div>
    </article>
    <a-spin v-else />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const md = new MarkdownIt({
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(str, { language: lang }).value
    }
    return ''
  }
})

const route = useRoute()
const article = ref(null)

const renderedContent = computed(() => {
  return article.value ? md.render(article.value.content) : ''
})

onMounted(async () => {
  const id = route.params.id
  // TODO: 加载文章详情
  // article.value = await api.getArticle(id)
})
</script>

<style scoped>
.article-detail h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
}

.article-meta {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 24px;
}

.article-meta span {
  margin-right: 16px;
}
</style>
