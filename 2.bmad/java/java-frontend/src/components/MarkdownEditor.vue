<template>
  <div class="markdown-editor">
    <div class="editor-toolbar">
      <a-space>
        <a-button size="small" @click="insertText('**', '**')">B</a-button>
        <a-button size="small" @click="insertText('*', '*')">I</a-button>
        <a-button size="small" @click="insertText('## ', '')">H2</a-button>
        <a-button size="small" @click="insertText('```\n', '\n```')">Code</a-button>
        <a-button size="small" @click="insertText('[', '](url)')">Link</a-button>
        <a-button size="small" @click="insertText('![alt](', ')')">Image</a-button>
      </a-space>
      <span class="save-status">{{ saveStatus }}</span>
    </div>
    
    <div class="editor-body">
      <div class="editor-pane">
        <textarea
          ref="editorRef"
          v-model="localContent"
          class="editor-textarea"
          placeholder="在此输入 Markdown 内容..."
          @input="handleInput"
        ></textarea>
      </div>
      
      <div class="preview-pane">
        <div class="preview-content article-content" v-html="renderedContent"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:content'])

const md = new MarkdownIt({
  html: true,
  linkify: true,
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(str, { language: lang }).value
    }
    return ''
  }
})

const editorRef = ref(null)
const localContent = ref(props.content)
const saveStatus = ref('')

let autoSaveTimer = null

const renderedContent = computed(() => {
  return md.render(localContent.value || '')
})

watch(() => props.content, (newVal) => {
  localContent.value = newVal
})

const handleInput = () => {
  emit('update:content', localContent.value)
  saveStatus.value = '编辑中...'
  
  // Auto save indicator
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(() => {
    saveStatus.value = '已自动保存'
    setTimeout(() => {
      saveStatus.value = ''
    }, 2000)
  }, 30000)
}

const insertText = (before, after) => {
  const textarea = editorRef.value
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = localContent.value.substring(start, end)
  
  const newText = 
    localContent.value.substring(0, start) + 
    before + selected + after + 
    localContent.value.substring(end)
  
  localContent.value = newText
  emit('update:content', localContent.value)
  
  // Restore cursor position
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selected.length)
  }, 0)
}

onUnmounted(() => {
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
})
</script>

<style scoped>
.markdown-editor {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-bottom: 1px solid #d9d9d9;
}

.save-status {
  font-size: 12px;
  color: #8c8c8c;
}

.editor-body {
  display: flex;
  height: 400px;
}

.editor-pane {
  flex: 1;
  border-right: 1px solid #d9d9d9;
}

.editor-textarea {
  width: 100%;
  height: 100%;
  padding: 16px;
  border: none;
  resize: none;
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
}

.preview-pane {
  flex: 1;
  padding: 16px;
  overflow: auto;
  background: #fff;
}

.preview-content {
  max-width: 100%;
}
</style>
