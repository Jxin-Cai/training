<template>
  <div class="frontend-layout">
    <h1>{{ category?.name || '分类' }}</h1>
    <p class="category-desc">{{ category?.description }}</p>
    
    <div class="article-list">
      <ArticleCard
        v-for="article in articles"
        :key="article.id"
        :article="article"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const category = ref(null)
const articles = ref([])

onMounted(async () => {
  const id = route.params.id
  // TODO: 加载分类和文章
})
</script>

<style scoped>
h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.category-desc {
  color: #8c8c8c;
  margin-bottom: 24px;
}
</style>
