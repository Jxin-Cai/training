import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../../api'

export function CategoryForm() {
  const { id } = useParams<{ id: string }>()
  const [name, setName] = useState('')
  const [slug, setSlug] = useState('')
  const [sortOrder, setSortOrder] = useState(0)
  const navigate = useNavigate()

  useEffect(() => {
    if (id && id !== 'new') {
      api.getCategories().then(cats => {
        const cat = cats.find((c: any) => c.id === parseInt(id))
        if (cat) {
          setName(cat.name)
          setSlug(cat.slug)
          setSortOrder(cat.sort_order)
        }
      })
    }
  }, [id])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const data = { name, slug, sort_order: sortOrder }
    if (id && id !== 'new') {
      await api.updateCategory(parseInt(id), data)
    } else {
      await api.createCategory(data)
    }
    navigate('/admin/categories')
  }

  return (
    <div className="container" style={{ maxWidth: 600 }}>
      <h1>{id && id !== 'new' ? 'Edit' : 'New'} Category</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <label>Name</label>
          <input value={name} onChange={e => setName(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Slug</label>
          <input value={slug} onChange={e => setSlug(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Sort Order</label>
          <input type="number" value={sortOrder} onChange={e => setSortOrder(parseInt(e.target.value))} />
        </div>
        <button className="btn btn-primary" type="submit">Save</button>
      </form>
    </div>
  )
}