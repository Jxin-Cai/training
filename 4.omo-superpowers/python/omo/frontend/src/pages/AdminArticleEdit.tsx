import { useEffect, useState, useRef } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { api, type ArticleInput, type Category } from '../api/client'
import { marked } from 'marked'

export default function AdminArticleEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const fileInputRef = useRef<HTMLInputElement>(null)
  
  const [title, setTitle] = useState('')
  const [contentMd, setContentMd] = useState('')
  const [categoryId, setCategoryId] = useState(0)
  const [status, setStatus] = useState('draft')
  const [categories, setCategories] = useState<Category[]>([])
  const [previewHtml, setPreviewHtml] = useState('')
  const isEdit = !!id

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    api.getCategories().then(setCategories).catch(console.error)
    
    if (id) {
      api.getArticle(parseInt(id)).then((article) => {
        setTitle(article.title)
        setContentMd(article.content_md)
        setCategoryId(article.category_id)
        setStatus(article.status)
      }).catch(console.error)
    }
  }, [id, navigate])

  // Update preview when content changes
  useEffect(() => {
    const html = marked(contentMd) as string
    setPreviewHtml(html)
  }, [contentMd])

  const handleFileImport = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    // Validate file type
    if (!file.name.endsWith('.md') && file.type !== 'text/markdown' && file.type !== 'text/plain') {
      alert('Please select a Markdown file (.md)')
      return
    }

    // Validate file size (1MB limit)
    if (file.size > 1024 * 1024) {
      alert('File size must be less than 1MB')
      return
    }

    // Check if user wants to overwrite current content
    if (contentMd && !confirm('This will replace your current content. Continue?')) {
      return
    }

    const reader = new FileReader()
    reader.onload = (event) => {
      const text = event.target?.result as string
      setContentMd(text)
      
      // Auto-fill title from filename (remove .md extension)
      if (!title) {
        const fileName = file.name.replace(/\.md$/, '')
        setTitle(fileName)
      }
    }
    reader.onerror = () => {
      alert('Failed to read file')
    }
    reader.readAsText(file, 'utf-8')
    
    // Reset file input
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    // Backend will render markdown, so we only send markdown content
    const data: ArticleInput = {
      title,
      content_md: contentMd,
      category_id: categoryId,
      status,
    }
    
    try {
      if (isEdit) {
        await api.updateArticle(parseInt(id!), data)
      } else {
        await api.createArticle(data)
      }
      navigate('/admin/articles')
    } catch (err) {
      alert('Failed to save article')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/admin/articles">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>{isEdit ? 'Edit Article' : 'New Article'}</h1>
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '15px' }}>
            <label>Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              required
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Category</label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(parseInt(e.target.value))}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              required
            >
              <option value={0}>Select category</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Status</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="draft">Draft</option>
              <option value="published">Published</option>
            </select>
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>
              Import Markdown File
            </label>
            <input
              ref={fileInputRef}
              type="file"
              accept=".md,text/markdown,text/plain"
              onChange={handleFileImport}
              style={{ marginTop: '5px' }}
            />
            <p style={{ color: '#666', fontSize: '12px', marginTop: '5px' }}>
              Select a .md file to import content (max 1MB)
            </p>
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Content (Markdown)</label>
            <textarea
              value={contentMd}
              onChange={(e) => setContentMd(e.target.value)}
              style={{ width: '100%', height: '300px', padding: '8px', marginTop: '5px', fontFamily: 'monospace' }}
              required
            />
          </div>
          {contentMd && (
            <div style={{ marginBottom: '15px' }}>
              <label>Preview</label>
              <div 
                style={{ 
                  width: '100%', 
                  height: '300px', 
                  padding: '8px', 
                  marginTop: '5px', 
                  border: '1px solid #ccc',
                  overflow: 'auto',
                  backgroundColor: '#f9f9f9'
                }}
                dangerouslySetInnerHTML={{ __html: previewHtml }}
              />
            </div>
          )}
          <button type="submit" style={{ padding: '10px 20px' }}>Save</button>
        </form>
      </main>
    </div>
  )
}