<template>
  <div class="content-editor">
    <h2>{{ isEdit ? '编辑内容' : '新建内容' }}</h2>

    <el-form :model="form" label-width="80px" v-loading="loading">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="请输入标题" />
      </el-form-item>

      <el-form-item label="分类" required>
        <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
          <el-option
            v-for="cat in flatCategories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="内容" required>
        <div class="editor-container">
          <div class="editor-toolbar">
            <div class="toolbar-left">
              <el-switch
                v-model="isMarkdownMode"
                active-text="Markdown"
                inactive-text="富文本"
                @change="toggleEditorMode"
              />
            </div>
            <div class="toolbar-buttons" v-if="editor && !isMarkdownMode">
              <el-button
                size="small"
                @click="editor.chain().focus().toggleBold().run()"
                :type="editor.isActive('bold') ? 'primary' : ''"
              >
                <strong>B</strong>
              </el-button>
              <el-button
                size="small"
                @click="editor.chain().focus().toggleItalic().run()"
                :type="editor.isActive('italic') ? 'primary' : ''"
              >
                <em>I</em>
              </el-button>
              <el-button
                size="small"
                @click="editor.chain().focus().toggleHeading({ level: 1 }).run()"
                :type="editor.isActive('heading', { level: 1 }) ? 'primary' : ''"
              >
                H1
              </el-button>
              <el-button
                size="small"
                @click="editor.chain().focus().toggleHeading({ level: 2 }).run()"
                :type="editor.isActive('heading', { level: 2 }) ? 'primary' : ''"
              >
                H2
              </el-button>
              <el-button
                size="small"
                @click="editor.chain().focus().toggleBulletList().run()"
                :type="editor.isActive('bulletList') ? 'primary' : ''"
              >
                列表
              </el-button>
              <el-button
                size="small"
                @click="insertCodeBlock"
                :type="editor.isActive('codeBlock') ? 'primary' : ''"
              >
                代码
              </el-button>
              <el-button
                size="small"
                @click="editor.chain().focus().toggleBlockquote().run()"
                :type="editor.isActive('blockquote') ? 'primary' : ''"
              >
                引用
              </el-button>
              <el-button
                size="small"
                @click="insertLink"
              >
                链接
              </el-button>
              <el-button
                size="small"
                @click="insertTable"
              >
                表格
              </el-button>
              <el-button size="small" @click="triggerImageUpload">
                图片
              </el-button>
            </div>
            <div class="toolbar-buttons" v-if="isMarkdownMode">
              <el-button size="small" @click="insertMdTemplate('**', '**')">B</el-button>
              <el-button size="small" @click="insertMdTemplate('*', '*')">I</el-button>
              <el-button size="small" @click="insertMdTemplate('# ', '')">H1</el-button>
              <el-button size="small" @click="insertMdTemplate('## ', '')">H2</el-button>
              <el-button size="small" @click="insertMdTemplate('- ', '')">列表</el-button>
              <el-button size="small" @click="insertMdCodeBlock">代码</el-button>
              <el-button size="small" @click="insertMdTemplate('> ', '')">引用</el-button>
              <el-button size="small" @click="insertMdLink">链接</el-button>
              <el-button size="small" @click="insertMdTable">表格</el-button>
            </div>
            <input
              ref="fileInput"
              type="file"
              accept="image/jpeg,image/png,image/gif,image/webp"
              @change="handleImageUpload"
              style="display: none"
            />
          </div>
          
          <div v-if="isMarkdownMode" class="markdown-editor-container">
            <el-input
              v-model="markdownContent"
              type="textarea"
              :rows="15"
              placeholder="使用 Markdown 语法编写内容..."
              @input="onMarkdownInput"
              class="markdown-textarea"
            />
            <div class="markdown-preview" v-html="renderedMarkdown"></div>
          </div>
          <editor-content v-else :editor="editor" class="editor-content" />
        </div>
      </el-form-item>

      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio value="DRAFT">草稿</el-radio>
          <el-radio value="PUBLISHED">发布</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="saveContent">保存</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Placeholder from '@tiptap/extension-placeholder'
import { Markdown } from 'tiptap-markdown'
import imageCompression from 'browser-image-compression'
import contentService from '@/services/contentService'
import categoryService from '@/services/categoryService'
import imageService from '@/services/imageService'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const categories = ref([])
const fileInput = ref(null)
const isMarkdownMode = ref(false)
const markdownContent = ref('')

const form = ref({
  title: '',
  categoryId: null,
  body: '',
  status: 'DRAFT'
})

const flatCategories = computed(() => {
  const result = []
  function flatten(cats, level = 0) {
    cats.forEach(cat => {
      result.push({
        id: cat.id,
        name: '  '.repeat(level) + cat.name
      })
      if (cat.children && cat.children.length > 0) {
        flatten(cat.children, level + 1)
      }
    })
  }
  flatten(categories.value)
  return result
})

const editor = useEditor({
  content: '',
  extensions: [
    StarterKit,
    Image.configure({
      inline: true,
      allowBase64: false
    }),
    Placeholder.configure({
      placeholder: '开始编写内容...'
    }),
    Markdown,
  ],
  onUpdate: ({ editor }) => {
    form.value.body = editor.getHTML()
  }
})

const renderedMarkdown = computed(() => {
  if (!markdownContent.value) return ''
  return simpleMarkdownToHtml(markdownContent.value)
})

function simpleMarkdownToHtml(md) {
  let html = md
    .replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code class="language-$1">$2</code></pre>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/^> (.+)$/gm, '<blockquote>$1</blockquote>')
    .replace(/^- (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>')
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')
    .replace(/!\[([^\]]*)\]\(([^)]+)\)/g, '<img src="$2" alt="$1" />')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
  
  if (!html.startsWith('<')) {
    html = '<p>' + html + '</p>'
  }
  return html
}

function toggleEditorMode() {
  if (isMarkdownMode.value) {
    if (editor.value) {
      const md = markdownContent.value
      editor.value.commands.setContent(simpleMarkdownToHtml(md), false)
      form.value.body = editor.value.getHTML()
    }
  } else {
    if (editor.value) {
      markdownContent.value = editor.value.storage.markdown.getMarkdown()
    }
  }
}

function onMarkdownInput() {
  form.value.body = simpleMarkdownToHtml(markdownContent.value)
}

watch(() => form.value.body, (newBody) => {
  if (editor.value && !isMarkdownMode.value && newBody !== editor.value.getHTML()) {
    editor.value.commands.setContent(newBody || '', false)
  }
})

onMounted(async () => {
  await loadCategories()
  if (isEdit.value) {
    await loadContent()
  }
})

onBeforeUnmount(() => {
  if (editor.value) {
    editor.value.destroy()
  }
})

async function loadCategories() {
  try {
    const tree = await categoryService.getTree()
    categories.value = tree || []
  } catch (error) {
    console.error('Failed to load categories:', error)
    ElMessage.error('加载分类失败')
    categories.value = []
  }
}

async function loadContent() {
  loading.value = true
  try {
    const content = await contentService.getById(route.params.id)
    form.value = {
      title: content.title,
      categoryId: content.category?.id,
      body: content.body || '',
      status: content.status || 'DRAFT'
    }
    if (editor.value && content.body) {
      editor.value.commands.setContent(content.body, false)
      markdownContent.value = editor.value.storage.markdown.getMarkdown()
    }
  } catch (error) {
    console.error('Failed to load content:', error)
    ElMessage.error('加载内容失败')
    router.push('/admin/content')
  } finally {
    loading.value = false
  }
}

function triggerImageUpload() {
  fileInput.value?.click()
}

async function handleImageUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('只支持 JPEG, PNG, GIF, WebP 格式的图片')
    event.target.value = ''
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB')
    event.target.value = ''
    return
  }

  try {
    ElMessage.info('正在压缩图片...')
    const options = {
      maxSizeMB: 2,
      maxWidthOrHeight: 1920,
      useWebWorker: true
    }
    const compressedFile = await imageCompression(file, options)

    ElMessage.info('正在上传图片...')
    const result = await imageService.upload(compressedFile, (progress) => {
      console.log('Upload progress:', progress)
    })

    if (isMarkdownMode.value) {
      const imgMd = `![${file.name}](${result.url})`
      markdownContent.value += '\n' + imgMd + '\n'
      onMarkdownInput()
    } else if (editor.value) {
      editor.value.chain().focus()
        .setImage({ src: result.url })
        .run()
    }

    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('Image upload failed:', error)
    ElMessage.error('图片上传失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  }

  event.target.value = ''
}

function insertCodeBlock() {
  if (editor.value) {
    editor.value.chain().focus().toggleCodeBlock().run()
  }
}

function insertLink() {
  ElMessageBox.prompt('请输入链接地址', '插入链接', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^https?:\/\/.+/,
    inputErrorMessage: '请输入有效的URL'
  }).then(({ value }) => {
    if (editor.value) {
      editor.value.chain().focus().setLink({ href: value }).run()
    }
  }).catch(() => {})
}

function insertTable() {
  ElMessage.info('表格功能建议切换到 Markdown 模式使用，支持标准 Markdown 表格语法')
}

function insertMdTemplate(before, after) {
  const textarea = document.querySelector('.markdown-textarea textarea')
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selected = markdownContent.value.substring(start, end)
  const replacement = before + selected + after
  
  markdownContent.value = 
    markdownContent.value.substring(0, start) + 
    replacement + 
    markdownContent.value.substring(end)
  
  onMarkdownInput()
  
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selected.length)
  }, 0)
}

function insertMdCodeBlock() {
  const codeBlock = '\n```\n代码内容\n```\n'
  markdownContent.value += codeBlock
  onMarkdownInput()
}

function insertMdLink() {
  const link = '[链接文本](https://example.com)'
  markdownContent.value += link
  onMarkdownInput()
}

function insertMdTable() {
  const table = '\n| 列1 | 列2 | 列3 |\n|-----|-----|-----|\n| 内容 | 内容 | 内容 |\n'
  markdownContent.value += table
  onMarkdownInput()
}

async function saveContent() {
  if (!form.value.title) {
    ElMessage.error('请输入标题')
    return
  }
  if (!form.value.categoryId) {
    ElMessage.error('请选择分类')
    return
  }

  let body
  if (isMarkdownMode.value) {
    body = form.value.body || simpleMarkdownToHtml(markdownContent.value)
  } else {
    body = editor.value ? editor.value.getHTML() : form.value.body
  }
  
  if (!body || body === '<p></p>') {
    ElMessage.error('请输入内容')
    return
  }

  loading.value = true
  try {
    const data = {
      title: form.value.title,
      categoryId: form.value.categoryId,
      body: body,
      status: form.value.status
    }

    if (isEdit.value) {
      await contentService.update(route.params.id, data)
      ElMessage.success('内容已更新')
    } else {
      await contentService.create(data)
      ElMessage.success('内容已创建')
    }

    router.push('/admin/content')
  } catch (error) {
    console.error('Save failed:', error)
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/admin/content')
}
</script>

<style scoped>
.content-editor {
  background: white;
  padding: 2rem;
  border-radius: 4px;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
  padding: 0.5rem;
  background: #f5f5f5;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.editor-content {
  min-height: 300px;
  padding: 1rem;
}

.editor-content :deep(.ProseMirror) {
  outline: none;
  min-height: 280px;
}

.editor-content :deep(.ProseMirror p.is-editor-empty:first-child::before) {
  color: #adb5bd;
  content: attr(data-placeholder);
  float: left;
  height: 0;
  pointer-events: none;
}

.editor-content :deep(.ProseMirror img) {
  max-width: 100%;
  height: auto;
  margin: 1rem 0;
}

.editor-content :deep(.ProseMirror h1) {
  font-size: 2em;
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror h2) {
  font-size: 1.5em;
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror ul),
.editor-content :deep(.ProseMirror ol) {
  padding-left: 2em;
  margin: 0.5em 0;
}

.editor-content :deep(.ProseMirror blockquote) {
  border-left: 3px solid #ddd;
  padding-left: 1rem;
  margin-left: 0;
  color: #666;
}

.editor-content :deep(.ProseMirror pre) {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
}

.editor-content :deep(.ProseMirror code) {
  background: #f5f5f5;
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: monospace;
}

.editor-content :deep(.ProseMirror table) {
  border-collapse: collapse;
  width: 100%;
  margin: 1rem 0;
}

.editor-content :deep(.ProseMirror th),
.editor-content :deep(.ProseMirror td) {
  border: 1px solid #ddd;
  padding: 0.5rem;
}

.editor-content :deep(.ProseMirror th) {
  background: #f5f5f5;
  font-weight: bold;
}

.markdown-editor-container {
  display: flex;
  min-height: 400px;
}

.markdown-textarea {
  flex: 1;
}

.markdown-textarea :deep(textarea) {
  border: none;
  border-radius: 0;
  resize: none;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.6;
}

.markdown-preview {
  flex: 1;
  padding: 1rem;
  border-left: 1px solid #dcdfe6;
  background: #fafafa;
  overflow-y: auto;
  max-height: 400px;
}

.markdown-preview :deep(h1) {
  font-size: 1.8em;
  margin: 0.5em 0;
}

.markdown-preview :deep(h2) {
  font-size: 1.4em;
  margin: 0.5em 0;
}

.markdown-preview :deep(h3) {
  font-size: 1.2em;
  margin: 0.5em 0;
}

.markdown-preview :deep(pre) {
  background: #f0f0f0;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
}

.markdown-preview :deep(code) {
  background: #f0f0f0;
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
}

.markdown-preview :deep(blockquote) {
  border-left: 3px solid #ddd;
  padding-left: 1rem;
  margin-left: 0;
  color: #666;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: 2em;
}

.markdown-preview :deep(table) {
  border-collapse: collapse;
  width: 100%;
}

.markdown-preview :deep(th),
.markdown-preview :deep(td) {
  border: 1px solid #ddd;
  padding: 0.5rem;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  height: auto;
}
</style>
