<template>
  <div class="article-card" @click="$router.push(`/article/${article.id}`)">
    <h3 class="title">{{ article.title }}</h3>
    <p class="summary">{{ article.summary }}</p>
    <div class="meta">
      <span class="category">{{ article.categoryName }}</span>
      <span class="author">{{ article.authorName }}</span>
      <span class="date">{{ article.createdAt }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  article: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.article-card {
  padding: 20px;
  margin-bottom: 16px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.article-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #262626;
}

.summary {
  color: #595959;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta {
  font-size: 14px;
  color: #8c8c8c;
}

.meta span {
  margin-right: 16px;
}
</style>
