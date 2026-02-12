<template>
  <div class="markdown-editor">
    <div class="editor-container">
      <div class="editor-pane">
        <div class="pane-header">Markdown编辑</div>
        <el-input
          v-model="content"
          type="textarea"
          :rows="20"
          placeholder="请输入Markdown内容..."
          @input="handleInput"
        />
      </div>
      <div class="preview-pane">
        <div class="pane-header">实时预览</div>
        <div class="preview-content" v-html="renderedHtml"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import MarkdownIt from 'markdown-it'

export default {
  name: 'MarkdownEditor',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const content = ref(props.modelValue)
    const md = new MarkdownIt()
    const renderedHtml = ref('')

    // 渲染Markdown
    const renderMarkdown = () => {
      renderedHtml.value = md.render(content.value || '')
    }

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      content.value = newVal
      renderMarkdown()
    }, { immediate: true })

    // 输入事件处理
    const handleInput = () => {
      emit('update:modelValue', content.value)
      renderMarkdown()
    }

    return {
      content,
      renderedHtml,
      handleInput
    }
  }
}
</script>

<style scoped>
.markdown-editor {
  width: 100%;
}

.editor-container {
  display: flex;
  gap: 20px;
  height: 600px;
}

.editor-pane,
.preview-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.pane-header {
  padding: 10px 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  font-weight: bold;
  color: #606266;
}

.preview-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background: white;
}

.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) {
  margin-top: 20px;
  margin-bottom: 10px;
}

.preview-content :deep(p) {
  margin-bottom: 10px;
  line-height: 1.6;
}

.preview-content :deep(code) {
  padding: 2px 4px;
  background: #f5f7fa;
  border-radius: 3px;
}

.preview-content :deep(pre) {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
