import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { api, type ArticleInput, type Category } from '../api/client'
import { marked } from 'marked'

export default function AdminArticleEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [contentMd, setContentMd] = useState('')
  const [categoryId, setCategoryId] = useState(0)
  const [status, setStatus] = useState('draft')
  const [categories, setCategories] = useState<Category[]>([])
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const contentHtml = marked(contentMd) as string
    const data: ArticleInput = {
      title,
      content_md: contentMd,
      content_html: contentHtml,
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
            <label>Content (Markdown)</label>
            <textarea
              value={contentMd}
              onChange={(e) => setContentMd(e.target.value)}
              style={{ width: '100%', height: '300px', padding: '8px', marginTop: '5px', fontFamily: 'monospace' }}
              required
            />
          </div>
          <button type="submit" style={{ padding: '10px 20px' }}>Save</button>
        </form>
      </main>
    </div>
  )
}