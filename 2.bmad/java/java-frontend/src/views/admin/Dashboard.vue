<template>
  <div class="dashboard">
    <h1>仪表盘</h1>
    <a-row :gutter="16">
      <a-col :span="8">
        <a-card>
          <a-statistic title="文章总数" :value="stats.articles" />
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card>
          <a-statistic title="分类数量" :value="stats.categories" />
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card>
          <a-statistic title="用户数量" :value="stats.users" />
        </a-card>
      </a-col>
    </a-row>
    
    <a-card title="最近文章" style="margin-top: 24px">
      <a-list :data-source="recentArticles">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta :title="item.title" :description="item.createdAt" />
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  articles: 0,
  categories: 0,
  users: 0
})

const recentArticles = ref([])

onMounted(async () => {
  // TODO: 加载统计数据
})
</script>

<style scoped>
.dashboard h1 {
  margin-bottom: 24px;
}
</style>
