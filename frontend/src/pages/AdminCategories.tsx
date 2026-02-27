import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, Category } from '../api/client'

export default function AdminCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [newName, setNewName] = useState('')
  const navigate = useNavigate()

  const loadCategories = () => {
    api.getCategories().then(setCategories).catch(console.error)
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    loadCategories()
  }, [navigate])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.createCategory(newName)
      setNewName('')
      loadCategories()
    } catch (err) {
      alert('Failed to create category')
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this category?')) return
    try {
      await api.deleteCategory(id)
      loadCategories()
    } catch (err) {
      alert('Failed to delete category')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/admin/articles">← Back to Articles</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>Categories</h1>
        <form onSubmit={handleCreate} style={{ margin: '20px 0' }}>
          <input
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="Category name"
            style={{ padding: '8px', marginRight: '10px' }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>Add</button>
        </form>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {categories.map((cat) => (
            <li key={cat.id} style={{ padding: '10px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
              <span>{cat.name}</span>
              <button onClick={() => handleDelete(cat.id)} style={{ color: 'red' }}>Delete</button>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}