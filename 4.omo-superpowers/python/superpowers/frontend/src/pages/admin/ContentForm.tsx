import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { api } from '../../api'

export function ContentForm() {
  const { id } = useParams<{ id: string }>()
  const [title, setTitle] = useState('')
  const [slug, setSlug] = useState('')
  const [categoryId, setCategoryId] = useState(0)
  const [markdown, setMarkdown] = useState('')
  const [categories, setCategories] = useState<any[]>([])
  const [preview, setPreview] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api.getCategories().then(setCategories).catch(console.error)
  }, [])

  useEffect(() => {
    if (id && id !== 'new') {
      api.getContents().then(contents => {
        const content = contents.find((c: any) => c.id === parseInt(id))
        if (content) {
          setTitle(content.title)
          setSlug(content.slug)
          setCategoryId(content.category_id || 0)
          setMarkdown(content.markdown_content || '')
        }
      })
    }
  }, [id])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const data = { title, slug, category_id: categoryId, markdown_content: markdown }
    if (id && id !== 'new') {
      await api.updateContent(parseInt(id), data)
    } else {
      await api.createContent(data)
    }
    navigate('/admin/contents')
  }

  return (
    <div className="container" style={{ maxWidth: 1200 }}>
      <h1>{id && id !== 'new' ? 'Edit' : 'New'} Content</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <label>Title</label>
          <input value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Slug</label>
          <input value={slug} onChange={e => setSlug(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Category</label>
          <select value={categoryId} onChange={e => setCategoryId(parseInt(e.target.value))}>
            <option value={0}>Select category</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>
        <div style={{ marginBottom: 10 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
            <label>Markdown Content</label>
            <button type="button" className="btn" onClick={() => setPreview(!preview)}>
              {preview ? 'Edit' : 'Preview'}
            </button>
          </div>
          {preview ? (
            <div style={{ border: '1px solid #ddd', minHeight: 400, padding: 20 }}>
              <ReactMarkdown>{markdown}</ReactMarkdown>
            </div>
          ) : (
            <textarea 
              value={markdown} 
              onChange={e => setMarkdown(e.target.value)} 
              rows={20}
              style={{ fontFamily: 'monospace' }}
            />
          )}
        </div>
        <button className="btn btn-primary" type="submit">Save</button>
      </form>
    </div>
  )
}